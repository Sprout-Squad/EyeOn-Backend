package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginReq;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;

public interface AuthService {
    KakaoLoginRes kakaoLogin(KakaoLoginReq kakaoLoginReq);
}
