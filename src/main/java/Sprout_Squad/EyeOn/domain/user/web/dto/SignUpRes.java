package Sprout_Squad.EyeOn.domain.user.web.dto;

public record SignUpRes(
        String token
) {
    public static SignUpRes from(String token) { return new SignUpRes(token); }
}
