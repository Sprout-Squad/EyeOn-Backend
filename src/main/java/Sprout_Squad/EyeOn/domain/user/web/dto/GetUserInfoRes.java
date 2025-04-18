package Sprout_Squad.EyeOn.domain.user.web.dto;

public record GetUserInfoRes(
        String username,
        String profileImageUrl,
        String address,
        String phoneNumber,
        String email
) {
    public static GetUserInfoRes of(String username,String profileImageUrl,String address,String phoneNumber,String email) {
        return new GetUserInfoRes(username,profileImageUrl,address,phoneNumber,email);
    }
}
