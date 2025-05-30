package Sprout_Squad.EyeOn.global.response;

import Sprout_Squad.EyeOn.global.response.code.BaseResponseCode;
import Sprout_Squad.EyeOn.global.response.code.GlobalSuccessCode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonPropertyOrder({"isSuccess", "timestamp", "code", "httpStatus", "message", "data"})
public class SuccessResponse<T> extends BaseResponse{
    private final int httpStatus;
    private final T data;

    @Builder
    public SuccessResponse(T data, BaseResponseCode baseResponseCode) {
        super(true, baseResponseCode.getCode(), baseResponseCode.getMessage());
        this.data = data;
        this.httpStatus = baseResponseCode.getHttpStatus();
    }

    public static <T> SuccessResponse<T> from(T data) {
        return new SuccessResponse<>(data, GlobalSuccessCode.SUCCESS_OK);
    }

    public static <T> SuccessResponse<T> of(T data, GlobalSuccessCode globalSuccessCode) {
        return new SuccessResponse<>(data, globalSuccessCode);
    }

    public static <T> SuccessResponse<T> empty() {
        return new SuccessResponse<>(null, GlobalSuccessCode.SUCCESS_OK);
    }

    public static <T> SuccessResponse<T> of(T data, BaseResponseCode baseResponseCode) {
        return new SuccessResponse<>(data, baseResponseCode);
    }

}
