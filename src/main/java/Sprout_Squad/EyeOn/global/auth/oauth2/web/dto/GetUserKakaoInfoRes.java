package Sprout_Squad.EyeOn.global.auth.oauth2.web.dto;


import java.util.Map;

public record GetUserKakaoInfoRes(
        Long id,
        String email,
        String profileImageUrl
){
    public static GetUserKakaoInfoRes of(Map<String, Object> kakaoResponse) {
        Long id = Long.valueOf(String.valueOf(kakaoResponse.get("id")));

        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoResponse.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String profileImageUrl = (String) profile.get("profile_image_url");

        return new GetUserKakaoInfoRes(id, email, profileImageUrl);

    }
}
