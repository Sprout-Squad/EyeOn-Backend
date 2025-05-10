package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum ExternalErrorCode implements BaseResponseCode {
    // clova ocr
    CLOVA_REQUEST_ERROR_500("CLOVA_REQUEST_ERROR_500", INTERNAL_SERVER_ERROR, "CLOVA OCR 호출 중 에러가 발생했습니다."),

    // open ai
    IMAGE_ENCODE_ERROR_500("IMAGE_ENCODE_ERROR_500", INTERNAL_SERVER_ERROR, "파일 인코딩 중 에러가 발생했습니다."),
    OPENAI_REQUEST_ERROR_500("OPENAI_REQUEST_ERROR_500", INTERNAL_SERVER_ERROR, "OpenAI 호출 중 에러가 발생했습니다."),

    // pdf box
    FONT_NOT_FOUND_ERROR_500("FONT_NOT_FOUND_ERROR_500", INTERNAL_SERVER_ERROR, "폰트 로드 중 에러가 발생했습니다."),
    FILE_CREATE_FAILED_500("FILE_CREATE_FAILED_500", INTERNAL_SERVER_ERROR, "파일을 생성하는 중 에러가 발생했습니다.");


    private final String code;
    private final int httpStatus;
    private final String message;
}
