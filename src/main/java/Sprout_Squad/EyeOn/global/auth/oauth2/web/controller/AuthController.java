package Sprout_Squad.EyeOn.global.auth.oauth2.web.controller;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.service.UserService;
import Sprout_Squad.EyeOn.global.auth.exception.AuthErrorCode;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import Sprout_Squad.EyeOn.global.auth.oauth2.service.KakaoService;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;
import Sprout_Squad.EyeOn.global.response.BaseResponse;
import Sprout_Squad.EyeOn.global.response.ErrorResponse;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final KakaoService kakaoService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/kakao/login")
    public BaseResponse kakaoLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");

         // 1. 인가 코드로 액세스 토큰 발급
        String accessToken = kakaoService.getAccessToken(code).accessToken();

        // 2. 발급받은 토큰으로 사용자 정보 조회
        GetUserInfoRes getUserInfoRes = kakaoService.getUserInfo(accessToken);
        Long kakaoId = getUserInfoRes.id();

        // 3. DB에 사용자 존재 여부 확인
        User user = userRepository.findByKakaoId(kakaoId);

        // 3-1. 존재하지 않는 사용자일 경우, UserNotFoundException
        if (user == null) {
            Map<String, Object> result = Map.of(
                    "accessToken", accessToken,
                    "kakaoId", kakaoId
//                    "profileImage",
            );
            return ErrorResponse.of(AuthErrorCode.USER_NOT_FOUND_404, "존재하지 않는 사용자입니다.", result);
        }

        // 4. 로그인에 성공하면 JWT 토큰 발급
        String jwt = jwtTokenProvider.createToken(user.getKakaoId());
        return SuccessResponse.of(new KakaoLoginRes(jwt));

    }

}
