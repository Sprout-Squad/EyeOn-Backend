package Sprout_Squad.EyeOn.domain.user.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class UserAlreadyExistException extends BaseException {
    public UserAlreadyExistException() {
        super(UserErrorCode.USER_ALREADY_EXIST_409);
    }
}
