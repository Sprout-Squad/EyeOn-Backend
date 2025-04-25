package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface FormService {
    GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId);
    List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, DocumentType documentType);
}