package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class S3UrlInvalidException extends BaseException {
    public S3UrlInvalidException() { super(ExternalErrorCode.S3_URL_INVALID_500); }
}
