package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.repository.FormRepository;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.global.auth.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Override
    public GetFormRes getOneForm(Long formId) {
        // 양식이 존재하지 않을 경우 ->  FormNotFoundException
        Form form = formRepository.getFormByFormId(formId);
        return GetFormRes.of(form);
    }

    @Override
    public List<GetFormRes> getAllFormsByType(DocumentType documentType) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = authenticationUserUtils.getCurrentUser();

        List<Form> formList = formRepository.findAllByUserAndFormType(user, documentType);

        return formList.stream()
                .map(GetFormRes::of)
                .toList();
    }
}
