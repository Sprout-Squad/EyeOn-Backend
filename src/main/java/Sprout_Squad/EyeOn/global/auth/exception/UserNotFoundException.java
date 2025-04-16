package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

import java.util.Map;

public class UserNotFoundException extends BaseException {
    private final Map<String, Object> extra;

    public UserNotFoundException(Map<String, Object> extra) {
        super(AuthErrorCode.USER_NOT_FOUND_404);
        this.extra = extra;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }
}
