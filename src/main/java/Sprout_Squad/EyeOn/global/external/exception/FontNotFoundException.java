package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class FontNotFoundException extends BaseException {
    public FontNotFoundException() { super(ExternalErrorCode.FONT_NOT_FOUND_ERROR_500); }
}
