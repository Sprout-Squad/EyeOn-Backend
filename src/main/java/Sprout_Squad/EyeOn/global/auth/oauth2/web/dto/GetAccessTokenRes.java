package Sprout_Squad.EyeOn.global.auth.oauth2.web.dto;

public record GetAccessTokenRes(
        String accessToken
){
    public static GetAccessTokenRes from(String accessToken) { return new GetAccessTokenRes(accessToken); }
}
