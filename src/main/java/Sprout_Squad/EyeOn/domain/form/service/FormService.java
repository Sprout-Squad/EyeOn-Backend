package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;

import java.util.List;

public interface FormService {
    GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId);
    List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, FormType formType);
}