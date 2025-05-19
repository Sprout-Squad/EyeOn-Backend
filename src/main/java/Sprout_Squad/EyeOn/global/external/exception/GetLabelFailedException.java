package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class GetLabelFailedException extends BaseException {
    public GetLabelFailedException() { super(ExternalErrorCode.GET_LABEL_FAILED_500); }
}
