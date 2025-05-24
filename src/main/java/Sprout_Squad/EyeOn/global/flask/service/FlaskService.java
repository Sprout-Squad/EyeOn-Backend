package Sprout_Squad.EyeOn.global.flask.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.ModifyDocumentReq;
import Sprout_Squad.EyeOn.domain.document.web.dto.ModifyDocumentReqWrapper;
import Sprout_Squad.EyeOn.domain.document.web.dto.WriteDocsReq;
import Sprout_Squad.EyeOn.domain.document.web.dto.WriteDocsReqWrapper;
import Sprout_Squad.EyeOn.global.flask.dto.GetFieldForModifyRes;
import Sprout_Squad.EyeOn.global.flask.dto.GetFieldForWriteRes;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.converter.ImgConverter;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelResForModify;
import Sprout_Squad.EyeOn.global.flask.exception.GetLabelFailedException;
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
     * 모델에서 문서 분석 결과를 받아 가공 (작성)
     */
    public GetModelRes getResFromModelForWrite(MultipartFile file, String fileName){
        try {
            String fileToBase64 = ImgConverter.toBase64(file);
            String fileExtension = pdfService.getFileExtension(fileName);

            // 플라스크 요청
            String jsonRes = getLabelForWrite(fileToBase64, fileExtension);

            // jsonRes를 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = mapper.readValue(jsonRes, Map.class);

            // tokens: 문서에서 추출된 텍스트 조각
            List<String> tokens = (List<String>) resultMap.get("tokens");

            // labels: 각 토큰의 labels
            List<String> labels = (List<String>) resultMap.get("labels");

            // doctype: 문서 유형
            String doctype = (String) resultMap.get("doctype");

            // rawBboxes: 각 토큰의 bounding box 좌표 정보
            List<List<?>> rawBboxes = (List<List<?>>) resultMap.get("bboxes");

            // bboxes: 각 토큰의 bounding box 좌표 정보
            List<List<Double>> bboxes = new ArrayList<>();

            // List<?> 형태로 파싱된 좌표 데이터를 List<Double>로 변환
            for (List<?> box : rawBboxes) {
                List<Double> convertedBox = box.stream()
                        .map(val -> ((Number) val).doubleValue()) // JSON 파싱 결과가 Number 타입 -> 형변환 필요
                        .toList();
                bboxes.add(convertedBox);
            }
            return GetModelRes.of(doctype, tokens, bboxes, labels);
        } catch (Exception e){ throw new GetLabelFailedException();}
    }

    /**
     * 작성을 위한 문서 필드 분석(라벨링) 요청
     */
    public String getLabelForWrite(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/create", base64Image, fileExt);
        Map result = (Map) responseBody.get("result");
        return objectMapper.writeValueAsString(result);  // result 전체를 JSON 문자열로 변환
    }

    /**
     * 모델에서 문서 분석 결과를 받아 가공 (수정)
     */
    public GetModelResForModify getResFromModelForModify(MultipartFile file, String fileName){
        try {
            String fileToBase64 = ImgConverter.toBase64(file);
            String fileExtension = pdfService.getFileExtension(fileName);

            // Flask에 요청
            String jsonRes = getLabelForModify(fileToBase64, fileExtension);

            // 응답 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> rootMap = mapper.readValue(jsonRes, Map.class);
            Map<String, Object> resultMap = (Map<String, Object>) rootMap.get("layoutlm_result");
            Map<String, Object> mergedMap = (Map<String, Object>) rootMap.get("merged_tokens");

            String doctype = (String) resultMap.get("doctype");
            List<String> tokens = (List<String>) resultMap.get("tokens");
            List<String> labels = (List<String>) resultMap.get("labels");

            List<List<Double>> bboxes = new ArrayList<>();
            List<List<?>> rawBboxes = (List<List<?>>) resultMap.get("bboxes");
            for (List<?> box : rawBboxes) {
                List<Double> converted = box.stream()
                        .map(val -> ((Number) val).doubleValue())
                        .toList();
                bboxes.add(converted);
            }

            List<String> mergedTokens = (List<String>) mergedMap.get("tokens");

            // 인덱스 생성
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < bboxes.size(); i++) {
                indices.add(i);
            }

            return new GetModelResForModify(doctype, tokens, bboxes, labels, indices, mergedTokens);

        } catch (Exception e) {
            throw new GetLabelFailedException();
        }
    }


    /**
     * 수정을 위한 문서 필드 분석(라벨링) 요청
     */
    public String getLabelForModify(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/modify", base64Image, fileExt);
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
     * 모델로부터 얻은 결과를 가공 및 사용자 정보를 채워넣어 문서 수정 양식에 맞는 형태로 반환
     */
    public List<GetFieldForModifyRes> getFieldForModify(GetModelResForModify getModelResForModify) {
        List<String> labels = getModelResForModify.labels();
        List<String> mergedTokens = getModelResForModify.mergedTokens();
        List<List<Double>> bboxes = getModelResForModify.bboxes();
        Map<String, String> labelMap = getLabelMap(getModelResForModify.doctype());

        List<GetFieldForModifyRes> results = new ArrayList<>();

        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            if (!label.endsWith("-FIELD")) continue;

            // FIELD를 기준으로 원래의 field명을 얻어냄
            String field = label.replace("-FIELD", "");
            String displayName = labelMap.getOrDefault(field, field);

            if (i >= mergedTokens.size()) continue;

            String value = mergedTokens.get(i);
            if (value == null || value.trim().equals("[BLANK]")) continue;

            List<Double> bbox = bboxes.get(i);
            results.add(new GetFieldForModifyRes(field, label, i, bbox, displayName, value));
        }

        return results;
    }



    /**
     * 모델로부터 얻은 결과를 가공 및 사용자 정보를 채워넣어 문서 작성 양식에 맞는 형태로 반환
     */
    public List<GetFieldForWriteRes> getFieldForWrite(GetModelRes getModelRes, UserPrincipal userPrincipal) {
        User user = userRepository.getUserById(userPrincipal.getId());
        Map<String, String> labelMap = getLabelMap(getModelRes.doctype());
        List<GetFieldForWriteRes> fields = new ArrayList<>();

        List<String> labels = getModelRes.labels();
        List<String> tokens = getModelRes.tokens();
        List<Integer> indices = getModelRes.indices();
        List<List<Double>> bboxes = getModelRes.bboxes();

        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);

            if (label.endsWith("-FIELD")) {
                String field = label.replace("-FIELD", "");
                String targetField = label;
                String displayName = labelMap.getOrDefault(field, field);
                String value = resolveValue(field, user);

                int actualIndex = indices.get(i);
                List<Double> bbox = bboxes.get(i);

                fields.add(new GetFieldForWriteRes(
                        field,
                        targetField,
                        actualIndex,
                        bbox,
                        displayName,
                        value
                ));
            }
        }

        return fields;
    }


    /**
     * 수정할 문서 분석 요청
     */
    public String getModifyLabelReq(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendJsonRequestToFlask("/api/ai/modify", base64Image, fileExt);
        Map result = (Map) responseBody.get("result");
        System.out.println(result);
        return objectMapper.writeValueAsString(result);
    }

    /**
     * 수정 문석 분석 결과를 바탕으로 modifyDocumentReq와 비교하여 WriteDocsReqWrapper 를 반환
     */
    public WriteDocsReqWrapper getModifyRes(String aiResult, ModifyDocumentReqWrapper modifyDocumentReqWrapper) {
        try {
            Map<String, Object> resultMap = objectMapper.readValue(aiResult, Map.class);

            List<String> labels = (List<String>) resultMap.get("labels");
            List<List<Double>> bboxes = (List<List<Double>>) resultMap.get("bboxes");
            List<String> tokens = (List<String>) resultMap.get("tokens");

            List<WriteDocsReq> resultList = new ArrayList<>();

            // 1. [BLANK] && -FIELD 필드만 추출
            List<Integer> targetIndices = new ArrayList<>();
            for (int i = 0; i < tokens.size(); i++) {
                if ("[BLANK]".equals(tokens.get(i)) && labels.get(i).endsWith("-FIELD")) {
                    targetIndices.add(i);
                }
            }

            // 2. 수정 요청 인덱스를 targetIndices를 기준으로 처리
            for (ModifyDocumentReq modify : modifyDocumentReqWrapper.data()) {
                int logicalIndex = modify.i(); // 클라이언트가 보낸 index
                String newValue = modify.v();

                if (logicalIndex < targetIndices.size()) {
                    int realIndex = targetIndices.get(logicalIndex); // 실제 모델 예측 결과 상 index
                    String targetField = labels.get(realIndex);
                    String field = targetField.replace("-FIELD", "");
                    String displayName = field;

                    resultList.add(new WriteDocsReq(
                            field,
                            targetField,
                            logicalIndex, // 클라이언트 요청 index 유지
                            bboxes.get(realIndex),
                            displayName,
                            newValue
                    ));
                }
            }

            return new WriteDocsReqWrapper(resultList);
        } catch (Exception e) {
            throw new RuntimeException("수정 필드 가공 실패", e);
        }
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
