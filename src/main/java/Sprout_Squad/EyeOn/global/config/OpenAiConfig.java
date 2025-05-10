package Sprout_Squad.EyeOn.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class OpenAiConfig {
    @Value("${application.openai.api-key}")
    private String apiKey;

    @Value("${application.openai.model}")
    private String model;

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public HttpHeaders createOpenAiHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(contentType); // application/json, multipart/form-data
        return headers;
    }

}
