package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class ClovaRequestException extends BaseException {
    public ClovaRequestException() { super(ExternalErrorCode.CLOVA_REQUEST_ERROR_500); }
}
