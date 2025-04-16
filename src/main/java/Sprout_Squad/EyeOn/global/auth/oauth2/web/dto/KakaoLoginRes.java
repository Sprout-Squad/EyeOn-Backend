package Sprout_Squad.EyeOn.global.auth.oauth2.web.dto;

public record KakaoLoginRes(
        String token
) {
    public static KakaoLoginRes from(String token) { return new KakaoLoginRes(token); }
}
