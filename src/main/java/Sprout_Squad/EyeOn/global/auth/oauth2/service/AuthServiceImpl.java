package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.UserSignupRequiredException;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserKakaoInfoRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginReq;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오로 로그인
     */
    @Override
    public KakaoLoginRes kakaoLogin(KakaoLoginReq kakaoLoginReq) {
        // 1. 인가 코드로 액세스 토큰 발급
        String accessToken = kakaoOAuthClient.getAccessToken(kakaoLoginReq.code()).accessToken();

        // 2. 발급받은 토큰으로 사용자 정보 조회
        GetUserKakaoInfoRes getUserKakaoInfoRes = kakaoOAuthClient.getUserInfo(accessToken);
        Long kakaoId = getUserKakaoInfoRes.id();

        // 3. DB에 사용자 존재 여부 확인
        Optional<User> user = userRepository.findByKakaoId(kakaoId);

        // 3-1. 존재하지 않는 사용자일 경우, UserNotFoundException
        if (user.isEmpty()) {
            throw new UserSignupRequiredException(Map.of(
                    "kakaoId", kakaoId,
                    "email", getUserKakaoInfoRes.email(),
                    "profileImageUrl", getUserKakaoInfoRes.profileImageUrl()
            ));
        }

        // 4. 로그인에 성공하면 JWT 토큰 발급
        String jwt = jwtTokenProvider.createToken(user.get().getId());
        return KakaoLoginRes.from(jwt);
    }
}
