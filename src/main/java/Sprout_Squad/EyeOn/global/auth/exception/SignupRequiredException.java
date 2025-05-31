package Sprout_Squad.EyeOn.global.auth.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

import java.util.Map;

public class SignupRequiredException extends BaseException {
    private final Map<String, Object> extra;

    public SignupRequiredException(Map<String, Object> extra) {
        super(AuthErrorCode.SIGN_UP_REQUIRED_404);
        this.extra = extra;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }
}
