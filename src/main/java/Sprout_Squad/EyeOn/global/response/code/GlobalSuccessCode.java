package Sprout_Squad.EyeOn.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static Sprout_Squad.EyeOn.global.constant.StaticValue.CREATED;
import static Sprout_Squad.EyeOn.global.constant.StaticValue.OK;


@Getter
@AllArgsConstructor
public enum GlobalSuccessCode implements BaseResponseCode {
    SUCCESS_OK( "SUCCESS_200", OK,"호출에 성공하였습니다."),
    SUCCESS_CREATED("CREATED_201", CREATED, "호출에 성공하였습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;

}
