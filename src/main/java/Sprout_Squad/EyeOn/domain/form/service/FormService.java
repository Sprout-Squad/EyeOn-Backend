package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FormService {
    UploadFormRes uploadForm(UserPrincipal userPrincipal, MultipartFile file) throws IOException;
    GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId);
    List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, FormType formType);
    FormType getFormType(MultipartFile file, String fileName);
    GetModelRes getResFromModel(MultipartFile file, String fileName);
}