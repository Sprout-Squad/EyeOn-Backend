package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetAdviceReq;
import Sprout_Squad.EyeOn.global.config.OpenAiConfig;
import Sprout_Squad.EyeOn.global.external.exception.OpenAiApiException;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import Sprout_Squad.EyeOn.global.flask.mapper.FieldLabelMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final OpenAiConfig openAiConfig;
    private final RestTemplate restTemplate;
    private final FieldLabelMapper fieldLabelMapper;

    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SUMMARY_PROMPT = "당신은 문서를 요약하는 능력이 아주 뛰어난 사람입니다."
            + "사진의 문서를 분석하고, 중요한 내용을 빠짐없이 요약해주세요. 반드시 모든 내용을 요약에 포함시켜주어야 합니다."
            + "텍스트로 제공할 것이기 때문에 강조 표시나 볼드 표시는 필요하지 않습니다.";

    private static final String MODIFY_PROMPT = "당신은 문서를 검토하는 능력이 매우 뛰어난 사람입니다. 해당 문서를 검토하세요"+
            "모든 항목을 분석할 필요는 없으며, 오타를 찾거나 잘못된 부분 지적하세요"+ "다음 JSON 배열은 문서를 구조화 시킨 내용입니다. " +
            "각 항목은 index(i), displayName(d:표시된 명칭), value(v:입력된 값)를 가지고 있습니다.\n" +
            "법적으로 살펴봐야 할 조언이나 문제가 될만할 부분을 간단하게 제시하세요." +
            "수정이 필요한 항목에 한해서만 대해 한국어 조언(a)를 추가하여 JSON으로 반환해 주세요. 그러나 ```json이라는 표시는 빼주세요. " +
            "조언을 제공할 항목의 d와 v값을 JSON 형태로 제공할 때 표시된 그대로가 아닌 알맞은 띄어쓰기를 제공해주세요. " +
            "그러나 이 내용이 a에 들어가서는 안됩니다. \n" +
            "형식은 아래와 같아야 합니다:\n" +
            "\n" +
            "[\n" +
            "  { \"i\": ..., \"d\": \"...\", \"v\": \"...\", \"a\": \"...\" },\n" +
            "  ...\n" +
            "]\n" +
            "\n" +
            "설명 없이 배열만 출력하세요.\n" +
            "입력은 다음과 같습니다. \n";

    /**
     * 수정할 부분 분석 요청
     */
    public String getModifyAnalyzeFromOpenAi(List<GetAdviceReq> fields, DocumentType documentType) {
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }

        System.out.println("📝 분석 요청 payload: " + json);

        String prompt = "이 문서의 유형은 " + documentType + "입니다.\n" + MODIFY_PROMPT + json;
        Map<String, Object> requestBody = createRequestBody(prompt);
        ResponseEntity<Map> response = sendRequest(requestBody);
        return parseResponse(response);
    }


    /**
     * 이미지 요약 요청
     */
    public String getSummaryFromOpenAi(String imageUrl) {
        Map<String, Object> requestBody = createRequestBodyWithImg(imageUrl, SUMMARY_PROMPT);
        ResponseEntity<Map> response = sendRequest(requestBody);
        return parseResponse(response);
    }

    /**
     * 프롬프트로만 RequestBody 생성
     */
    public Map<String, Object> createRequestBody(String prompt) {
        return Map.of(
                "model", openAiConfig.getModel(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );
    }


    /**
     * 이미지를 포함한 RequestBody 생성
     */
    public Map<String, Object> createRequestBodyWithImg(String imageUrl, String prompt){
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