package Sprout_Squad.EyeOn.global.flask.service;

import Sprout_Squad.EyeOn.global.flask.dto.GetFieldRes;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.converter.ImgConverter;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.flask.exception.TypeDetectedFiledException;
import Sprout_Squad.EyeOn.global.flask.mapper.FieldLabelMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FlaskService {
    private final RestTemplate restTemplate;
    private final PdfService pdfService;
    private final FieldLabelMapper fieldLabelMapper;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl = "http://3.39.215.178:5050";

    /**
     * 문서 필드 분석(라벨링) 요청
     */
    public String getLabel(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/create", base64Image, fileExt);
        Map result = (Map) responseBody.get("result");
        return objectMapper.writeValueAsString(result);  // result 전체를 JSON 문자열로 변환
    }

    /**
     * 문서 타입에 맞는 필드의 라벨명을 반환
     */
    private Map<String, String> getLabelMap(String docType){
        // context는 중첩된 Map 구조
        Map<String, Map<String, String>> context = fieldLabelMapper.getContextualLabels();

        // doctype에 맞는 Map을 복사
        Map<String, String> labelMap = new HashMap<>(context.getOrDefault(docType, Collections.emptyMap()));

        // common 항목에 대하여 Map을 복사
        Map<String, String> commonMap = context.getOrDefault("common", Collections.emptyMap());

        // labelMap에 공통 항목까지 삽입
        labelMap.putAll(commonMap);

        return labelMap;
    }

    /**
     * 필드 라벨에 따라 사용자 정보를 채워넣어 반환
     */
    private String resolveValue(String baseLabel, User user) {
        return switch (baseLabel) {
            case "B-PERSONAL-NAME", "B-GRANTOR-NAME", "B-DELEGATE-NAME", "B-NAME", "B-SIGN-NAME" -> user.getName();
            case "B-PERSONAL-RRN", "B-GRANTOR-RRN", "B-DELEGATE-RRN" -> user.getResidentNumber();
            case "B-PERSONAL-PHONE", "B-GRANTOR-PHONE", "B-DELEGATE-PHONE" -> user.getPhoneNumber();
            case "B-PERSONAL-ADDR", "B-GRANTOR-ADDR", "B-DELEGATE-ADDR" -> user.getAddress();
            case "B-PERSONAL-EMAIL" -> user.getEmail();
            default -> null; // 나머지는 프론트에서 받기
        };
    }


    /**
     * 모델 예측 결과를 보기 좋게 가공
     */
    private GetFieldRes buildField(int i, GetModelRes getModelRes, User user, Map<String, String> labelMap) {
        String label = getModelRes.labels().get(i);
        String baseLabel = label.replace("-FIELD", "");
        String displayName = labelMap.getOrDefault(baseLabel, baseLabel);
        String value = resolveValue(baseLabel, user);

        return new GetFieldRes(
                baseLabel,
                label,
                i,
                getModelRes.bboxes().get(i),
                displayName,
                value
        );
    }

    /**
     * 1. [BLANK] 이고 -FIELD로 끝나는 경우
     * 2. labelMap에 정의되어 있으면 결과로 포함
     */
    private boolean isBlankField(String token, String label, Map<String, String> labelMap) {
        return ("[BLANK]".equals(token) && label.endsWith("-FIELD"))
                || labelMap.containsKey(label);
    }

    /**
     * 모델로부터 얻은 결과를 가공 및 사용자 정보를 채워넣어 구조화된 형태로 반환
     */
    public List<GetFieldRes> getField(GetModelRes getModelRes, UserPrincipal userPrincipal){
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        Map<String, String> labelMap = getLabelMap(getModelRes.doctype());
        List<GetFieldRes> fields = new ArrayList<>();

        for (int i = 0; i < getModelRes.tokens().size(); i++) {
            if (isBlankField(getModelRes.tokens().get(i), getModelRes.labels().get(i), labelMap)) {
                fields.add(buildField(i, getModelRes, user, labelMap));
            }
        }

        return fields;
    }


    /**
     * 수정할 문서 분석
     */
    public String getModifyLabel(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/create", base64Image, fileExt);
        Map result = (Map) responseBody.get("modify");
        return objectMapper.writeValueAsString(result);
    }

    /**
     * 문서 유형 감지 바디 구성
     */
    public String detectType(MultipartFile file, String fileName) {
        try {
            String base64Image = ImgConverter.toBase64(file);
            String fileExt = pdfService.getFileExtension(fileName);
            Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/detect", base64Image, fileExt);
            return (String) responseBody.get("doc_type");
        } catch (Exception e) {
            throw new TypeDetectedFiledException();
        }
    }

    /**
     * 문서 작성, 필드 분석,  문서 수정 필드 분석 요청 처리 메서드
     */
    private Map<String, Object> sendJsonRequestToFlask(String path, String base64Image, String fileExt) {
        String endpoint = baseUrl + path;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image_base64", base64Image);
        requestBody.put("file_ext", fileExt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(endpoint, requestEntity, Map.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(responseEntity.getBody().get("isSuccess"))) {
                return responseEntity.getBody();
            } else {
                String msg = responseEntity.getBody() != null
                        ? responseEntity.getBody().get("message").toString()
                        : "응답 body 없음";
                throw new RuntimeException("Flask API 호출 실패: " + msg);
            }
        } catch (Exception e) {
            System.out.println("Flask 통신 중 예외 발생: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new RuntimeException("Flask API 요청 실패", e);
        }
    }
}
