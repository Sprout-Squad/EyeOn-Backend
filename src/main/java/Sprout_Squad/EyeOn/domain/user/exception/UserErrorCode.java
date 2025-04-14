package Sprout_Squad.EyeOn.domain.user.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.CONFLICT;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseResponseCode {
    USER_ALREADY_EXIST_409("USER_ALREADY_EXIST_409", CONFLICT, "이미 존재하는 사용자입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
