package Sprout_Squad.EyeOn.global.auth.oauth2.web.controller;

import Sprout_Squad.EyeOn.global.auth.oauth2.service.KakaoService;
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
    private final KakaoService kakaoService;

    @PostMapping("/kakao/login")
    public SuccessResponse<KakaoLoginRes> kakaoLogin(@RequestBody @Valid KakaoLoginReq kakaoLoginReq) {
        KakaoLoginRes res = kakaoService.kakaoLogin(kakaoLoginReq);
        return SuccessResponse.of(res, GlobalSuccessCode.SUCCESS_CREATED);
    }

}
