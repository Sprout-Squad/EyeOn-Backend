package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseResponseCode {
    INVALID_TOKEN_401("INVALID_TOKEN_401", UNAUTHORIZED, "토큰 값을 확인해주세요."),
    SIGN_UP_REQUIRED_404("SIGN_UP_REQUIRED_404", NOT_FOUND, "회원가입이 필요합니다."),
    CAN_NOT_ACCESS_403("CAN_NOT_ACCESS_403", FORBIDDEN, "접근 권한이 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}