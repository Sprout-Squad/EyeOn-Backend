package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.web.dto.GetOneFormRes;

public interface FormService {
    GetOneFormRes getOneForm(Long formId);
}