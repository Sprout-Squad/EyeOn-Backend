package Sprout_Squad.EyeOn.global.flask.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlaskService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl = "http://3.39.215.178:5050";

    /**
     * 문서 필드 분석 (라벨링)
     */
    public String getLabel(String base64Image, String fileExt) throws JsonProcessingException {
        Map<String, Object> responseBody = sendFlaskPostRequest("/api/ai/create", base64Image, fileExt);
        Map result = (Map) responseBody.get("result");
        System.out.println("결과 : " + result);
        return objectMapper.writeValueAsString(result);  // result 전체를 JSON 문자열로 변환
    }

    /**
     * 문서 유형 감지
     */
    public String detectType(String base64Image, String fileExt) {
        Map<String, Object> responseBody = sendFlaskPostRequest("/api/ai/detect", base64Image, fileExt);
        return (String) responseBody.get("doc_type");
    }

    /**
     * 공통 요청 처리 메서드
     */
    private Map<String, Object> sendFlaskPostRequest(String path, String base64Image, String fileExt) {
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
