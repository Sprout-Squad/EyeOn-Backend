package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.repository.FormRepository;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.form.web.dto.UploadFormRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.CanNotAccessException;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public UploadFormRes uploadForm(UserPrincipal userPrincipal, MultipartFile file, FormType formType) throws IOException {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        String fileName = s3Service.generateFileName(file);
        String fileUrl = s3Service.uploadFile(fileName, file);

        Form form = Form.toEntity(file, fileUrl, formType, user);
        formRepository.save(form);
        return UploadFormRes.of(form, s3Service.getSize(fileUrl));
    }

    @Override
    public GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 양식이 존재하지 않을 경우 ->  FormNotFoundException
        Form form = formRepository.getFormByFormId(formId);

        // 사용자의 양식이 아닐 경우 -> CanNotAccessException
        if(form.getUser() != user) throw new CanNotAccessException();

        return GetFormRes.of(form, s3Service.getSize(form.getImageUrl()));
    }

    @Override
    public List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, FormType formType) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        List<Form> formList = formRepository.findAllByUserAndFormType(user, formType);

        return formList.stream()
                .map(form -> {
                    long formSize = s3Service.getSize(form.getImageUrl());
                    return GetFormRes.of(form, formSize);
                })
                .toList();
    }
}
