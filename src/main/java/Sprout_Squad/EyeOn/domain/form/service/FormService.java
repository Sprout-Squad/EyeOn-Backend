package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.form.web.dto.UploadFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FormService {
    UploadFormRes uploadForm(UserPrincipal userPrincipal, MultipartFile file) throws IOException;
    GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId);
    List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, FormType formType);
}