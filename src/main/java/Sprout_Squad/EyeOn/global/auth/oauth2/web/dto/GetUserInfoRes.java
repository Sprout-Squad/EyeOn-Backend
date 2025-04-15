package Sprout_Squad.EyeOn.global.auth.oauth2.web.dto;

public record GetUserInfoRes(
        Long id,
        String email,
        String profileImageUrl
){}
