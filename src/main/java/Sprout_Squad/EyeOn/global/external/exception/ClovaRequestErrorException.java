package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class ClovaRequestErrorException extends BaseException {
    public ClovaRequestErrorException() { super(ExternalErrorCode.CLOVA_REQUEST_ERROR_500); }
}
