package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetAccessTokenRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginReq;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.KakaoLoginRes;

public interface KakaoService {
    GetAccessTokenRes getAccessToken(String code);
    GetUserInfoRes getUserInfo(String accessToken);
    KakaoLoginRes kakaoLogin(KakaoLoginReq kakaoLoginReq);
}
