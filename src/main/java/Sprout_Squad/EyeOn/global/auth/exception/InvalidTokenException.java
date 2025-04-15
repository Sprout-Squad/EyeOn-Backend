package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;
import Sprout_Squad.EyeOn.global.response.code.GlobalErrorCode;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(GlobalErrorCode globalErrorCode) {
        super(AuthErrorCode.INVALID_TOKEN_401);
    }
}
