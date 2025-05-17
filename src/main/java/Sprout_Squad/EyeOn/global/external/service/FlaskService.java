package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
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
    private String baseUrl = "http://3.39.215.178:5050";

    /**
     * 문서 유형 탐지
     */
    public String detectType(String base64Image, String fileExt){
        String endpoint = baseUrl + "/api/ai/detect";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image_base64", base64Image);
        requestBody.put("file_ext", fileExt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(endpoint, requestEntity, Map.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK && (Boolean) responseEntity.getBody().get("isSuccess")) {
            return (String) responseEntity.getBody().get("doc_type");
        } else {
            throw new RuntimeException("문서 유형 감지 실패: " + responseEntity.getBody().get("message"));
        }

    }
}
