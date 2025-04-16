package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException() {
        super(AuthErrorCode.INVALID_TOKEN_401);
    }
}
