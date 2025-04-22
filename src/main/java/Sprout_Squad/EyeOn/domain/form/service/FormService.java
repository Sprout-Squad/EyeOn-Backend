package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;

import java.util.List;

public interface FormService {
    GetFormRes getOneForm(Long formId);
    List<GetFormRes> getAllFormsByType(DocumentType documentType);
}