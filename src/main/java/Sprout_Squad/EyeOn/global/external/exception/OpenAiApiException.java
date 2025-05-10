package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class OpenAiApiException extends BaseException {
    public OpenAiApiException() {super(ExternalErrorCode.OPENAI_REQUEST_ERROR_500);}
}
