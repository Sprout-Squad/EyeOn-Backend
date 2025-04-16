package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.UserNotFoundException;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetAccessTokenRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginReq;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoApiKey;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    @Value("{KAKAO_CLIENT_SECRET}")
    private String kakaoClientSecret;


    /**
     * 인가 코드로 AccessToken 요청
     */
    @Override
    public GetAccessTokenRes getAccessToken(String code) {
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        String accessToken = response.getBody().get("access_token").toString();

        return GetAccessTokenRes.from(accessToken);
    }

    /**
     * AccessToken으로 사용자 정보 요청
     */
    @Override
    public GetUserInfoRes getUserInfo(String accessToken) {
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
        return GetUserInfoRes.from(body);
    }

    /**
     * 카카오로 로그인
     */
    @Override
    public KakaoLoginRes kakaoLogin(KakaoLoginReq kakaoLoginReq) {
        // 1. 인가 코드로 액세스 토큰 발급
        String accessToken = this.getAccessToken(kakaoLoginReq.code()).accessToken();

        // 2. 발급받은 토큰으로 사용자 정보 조회
        GetUserInfoRes getUserInfoRes = this.getUserInfo(accessToken);
        Long kakaoId = getUserInfoRes.id();

        // 3. DB에 사용자 존재 여부 확인
        Optional<User> user = userRepository.findByKakaoId(kakaoId);

        // 3-1. 존재하지 않는 사용자일 경우, UserNotFoundException
        if (user.isEmpty()) {
            throw new UserNotFoundException(Map.of(
                    "kakaoId", kakaoId,
                    "email", getUserInfoRes.email(),
                    "profileImageUrl", getUserInfoRes.profileImageUrl()
            ));
        }

        // 4. 로그인에 성공하면 JWT 토큰 발급
        String jwt = jwtTokenProvider.createToken(user.get().getKakaoId());
        return KakaoLoginRes.from(jwt);
    }
}
