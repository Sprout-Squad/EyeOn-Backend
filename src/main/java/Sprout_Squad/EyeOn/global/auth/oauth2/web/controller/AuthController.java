package Sprout_Squad.EyeOn.global.auth.oauth2.web.controller;

import Sprout_Squad.EyeOn.global.auth.oauth2.service.AuthServiceImpl;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginReq;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import Sprout_Squad.EyeOn.global.response.code.GlobalSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/kakao/login")
    public SuccessResponse<KakaoLoginRes> kakaoLogin(@RequestBody @Valid KakaoLoginReq kakaoLoginReq) {
        KakaoLoginRes res = authServiceImpl.kakaoLogin(kakaoLoginReq);
        return SuccessResponse.of(res, GlobalSuccessCode.SUCCESS_CREATED);
    }

}
