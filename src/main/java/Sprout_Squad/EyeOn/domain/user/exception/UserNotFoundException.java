package Sprout_Squad.EyeOn.domain.user.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND_404);
    }
}
