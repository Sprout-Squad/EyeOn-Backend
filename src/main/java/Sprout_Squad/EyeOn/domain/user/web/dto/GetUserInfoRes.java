package Sprout_Squad.EyeOn.domain.user.web.dto;

import Sprout_Squad.EyeOn.domain.user.entity.User;

public record GetUserInfoRes(
        String username,
        String profileImageUrl,
        String address,
        String phoneNumber,
        String email
) {
    public static GetUserInfoRes of(User user) {
        return new GetUserInfoRes(
                user.getName(),
                user.getProfileImageUrl(),
                user.getProfileImageUrl(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }
}
