package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.NOT_FOUND;
import static Sprout_Squad.EyeOn.global.constant.StaticValue.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseResponseCode {
    INVALID_TOKEN_401("GLOBAL_401_2", UNAUTHORIZED, "토큰 값을 확인해주세요."),
    USER_SIGN_UP_REQUIRED_404("USER_SIGN_UP_REQUIRED_404", NOT_FOUND, "존재하지 않는 사용자입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}