package Sprout_Squad.EyeOn.domain.user.web.dto;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.entity.enums.Gender;

public record GetUserInfoRes(
        String username,
        Gender gender,
        String residentNumber,
        String profileImageUrl,
        String address,
        String phoneNumber,
        String email
) {
    public static GetUserInfoRes from(User user) {
        return new GetUserInfoRes(
                user.getName(),
                user.getGender(),
                user.getResidentNumber(),
                user.getProfileImageUrl(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }
}
