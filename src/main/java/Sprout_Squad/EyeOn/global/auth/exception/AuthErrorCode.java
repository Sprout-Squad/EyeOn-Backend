package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseResponseCode {
    INVALID_TOKEN_401("GLOBAL_401_2", UNAUTHORIZED, "토큰 값을 확인해주세요.");

    private final String code;
    private final int httpStatus;
    private final String message;
}