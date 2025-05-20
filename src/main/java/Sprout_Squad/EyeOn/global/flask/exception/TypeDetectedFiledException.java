package Sprout_Squad.EyeOn.global.flask.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;
import Sprout_Squad.EyeOn.global.external.exception.ExternalErrorCode;

public class TypeDetectedFiledException extends BaseException {
    public TypeDetectedFiledException() { super(FlaskErrorCode.TYPE_DETECTED_FAILED_500); }
}
