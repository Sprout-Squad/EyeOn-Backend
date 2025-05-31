package Sprout_Squad.EyeOn.global.flask.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum FlaskErrorCode implements BaseResponseCode {
    // flask
    TYPE_DETECTED_FAILED_500("TYPE_DETECTED_FAILED_500", INTERNAL_SERVER_ERROR,"문서 유형 감지 중 에러가 발생했습니다."),
    GET_LABEL_FAILED_500("GET_LABEL_FAILED_500", INTERNAL_SERVER_ERROR, "문서 라벨링 중 에러가 발생했습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
