package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetAccessTokenRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserKakaoInfoRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuthClientImpl implements KakaoOAuthClient {
    private final RestTemplate restTemplate;

    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoApiKey;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;


    /**
     * 인가 코드로 AccessToken 요청
     */
    @Override
    public GetAccessTokenRes getAccessToken(String code) {
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        System.out.println("▶️ 인가 코드 확인: " + code);
        System.out.println("▶️ 리디렉션 URI: " + kakaoRedirectUri);
        System.out.println("▶️ 클라이언트 ID: " + kakaoApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        System.out.println("📦 요청 파라미터:");
        params.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            System.out.println("✅ 카카오 AccessToken 응답: " + body);

            String accessToken = body.get("access_token").toString();
            return GetAccessTokenRes.from(accessToken);
        } catch (Exception e) {
            System.err.println("❌ 카카오 토큰 요청 중 예외 발생:");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * AccessToken으로 사용자 정보 요청
     */
    @Override
    public GetUserKakaoInfoRes getUserInfo(String accessToken) {
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                kakaoUserInfoUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("id") == null) {
            throw new RuntimeException("카카오 사용자 정보 응답이 올바르지 않습니다.");
        }
        return GetUserKakaoInfoRes.of(body);
    }
}
