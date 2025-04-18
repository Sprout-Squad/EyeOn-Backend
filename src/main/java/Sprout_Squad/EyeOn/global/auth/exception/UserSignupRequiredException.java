package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

import java.util.Map;

public class UserSignupRequiredException extends BaseException {
    private final Map<String, Object> extra;

    public UserSignupRequiredException(Map<String, Object> extra) {
        super(AuthErrorCode.USER_SIGN_UP_REQUIRED_404);
        this.extra = extra;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }
}
