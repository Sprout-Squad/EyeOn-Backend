package Sprout_Squad.EyeOn.global.response;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonPropertyOrder({"isSuccess", "timestamp", "code", "httpStatus", "message"})
public class ErrorResponse extends BaseResponse {
    private final int httpStatus;

    @Builder
    public ErrorResponse(String code, String message, int httpStatus) {
        super(false, code, message);
        this.httpStatus = httpStatus;
    }

    // 기본 메시지를 그대로 사용할 떄
    public static ErrorResponse of(BaseResponseCode baseCode) {
        return ErrorResponse.builder()
                .code(baseCode.getCode())
                .message(baseCode.getMessage())
                .httpStatus(baseCode.getHttpStatus())
                .build();
    }

    // 기본 메시지를 커스터마이징 할 때
    public static ErrorResponse of(BaseResponseCode baseCode, String message) {
        return ErrorResponse.builder()
                .code(baseCode.getCode())
                .httpStatus(baseCode.getHttpStatus())
                .message(message)
                .build();
    }
}
