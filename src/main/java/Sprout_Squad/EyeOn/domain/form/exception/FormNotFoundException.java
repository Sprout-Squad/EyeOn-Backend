package Sprout_Squad.EyeOn.domain.form.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class FormNotFoundException extends BaseException {
    public FormNotFoundException(){super(FormErrorCode.FORM_NOT_FOUND_404);}
}
