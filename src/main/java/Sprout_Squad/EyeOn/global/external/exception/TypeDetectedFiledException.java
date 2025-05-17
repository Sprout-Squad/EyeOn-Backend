package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class TypeDetectedFiledException extends BaseException {
    public TypeDetectedFiledException() { super(ExternalErrorCode.TYPE_DETECTED_FAIL_500); }
}
