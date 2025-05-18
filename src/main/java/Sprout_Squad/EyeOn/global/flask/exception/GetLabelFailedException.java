package Sprout_Squad.EyeOn.global.flask.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;
import Sprout_Squad.EyeOn.global.external.exception.ExternalErrorCode;

public class GetLabelFailedException extends BaseException {
    public GetLabelFailedException() { super(FlaskErrorCode.GET_LABEL_FAILED_500); }
}
