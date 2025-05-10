package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.global.config.OpenAiConfig;
import Sprout_Squad.EyeOn.global.external.exception.OpenAiApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final OpenAiConfig openAiConfig;
    private final RestTemplate restTemplate;

    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT = "당신은 문서를 요약하는 능력이 아주 뛰어난 사람입니다."
            + "사진의 문서를 분석하고, 중요한 내용을 빠짐없이 요약해주세요. 반드시 모든 내용을 요약에 포함시켜주어야 합니다."
            + "텍스트로 제공할 것이기 때문에 강조 표시나 볼드 표시는 필요하지 않습니다.";

    /**
     * 이미지 요청
     */
    public String getSummaryFromOpenAi(String imageUrl) {
        Map<String, Object> requestBody = createRequestBody(imageUrl, SUMMARY_PROMPT);
        ResponseEntity<Map> response = sendRequest(requestBody);
        return parseResponse(response);

    }

    /**
     * RequestBody 생성
     */
    public Map<String, Object> createRequestBody(String imageUrl, String prompt){
        return Map.of(
                "model", openAiConfig.getModel(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "image_url",
                                                "image_url", Map.of("url", imageUrl)
                                        ),
                                        Map.of(
                                                "type", "text",
                                                "text", prompt
                                        )
                                )
                        )
                )
        );
    }

    /**
     * 요청 전송
     */
    public ResponseEntity<Map> sendRequest(Map<String, Object> requestBody){
        HttpHeaders headers = openAiConfig.createOpenAiHeaders(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(CHAT_URL, HttpMethod.POST, request, Map.class);
        } catch (Exception e) {
            throw new OpenAiApiException();
        }
    }

    /**
     * 응답 파싱
     */
    public String parseResponse(ResponseEntity<Map> response){
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}