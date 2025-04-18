package Sprout_Squad.EyeOn.domain.user.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.CONFLICT;
import static Sprout_Squad.EyeOn.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseResponseCode {
    USER_NOT_FOUND_404("USER_NOT_FOUND_404", NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXIST_409("USER_ALREADY_EXIST_409", CONFLICT, "이미 존재하는 사용자입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
