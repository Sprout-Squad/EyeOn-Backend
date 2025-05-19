package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class UnsupportedFileTypeException extends BaseException {
    public UnsupportedFileTypeException() { super(ExternalErrorCode.UNSUPPORTED_FILE_TYPE_400); }
}
