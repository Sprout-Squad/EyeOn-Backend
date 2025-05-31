package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class CanNotAccessException extends BaseException {
    public CanNotAccessException() { super(AuthErrorCode.CAN_NOT_ACCESS_403); }
}
