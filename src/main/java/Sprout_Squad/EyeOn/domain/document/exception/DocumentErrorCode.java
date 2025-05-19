package Sprout_Squad.EyeOn.domain.document.exception;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum DocumentErrorCode implements BaseResponseCode {
    DOCUMENT_NOT_FOUND_404("DOCUMENT_NOT_FOUND_404", NOT_FOUND, "문서를 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
