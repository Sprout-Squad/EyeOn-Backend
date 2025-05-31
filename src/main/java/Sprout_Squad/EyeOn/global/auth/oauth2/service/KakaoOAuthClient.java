package Sprout_Squad.EyeOn.global.auth.oauth2.service;

import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetAccessTokenRes;
import Sprout_Squad.EyeOn.global.auth.oauth2.web.dto.GetUserKakaoInfoRes;

public interface KakaoOAuthClient {
    GetAccessTokenRes getAccessToken(String code);
    GetUserKakaoInfoRes getUserInfo(String accessToken);
}
