package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.repository.FormRepository;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.CanNotAccessException;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.auth.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final UserRepository userRepository;

    @Override
    public GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 양식이 존재하지 않을 경우 ->  FormNotFoundException
        Form form = formRepository.getFormByFormId(formId);

        // 사용자의 양식이 아닐 경우 -> CanNotAccessException
        if(form.getUser() != user) throw new CanNotAccessException();

        return GetFormRes.of(form);
    }

    @Override
    public List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, DocumentType documentType) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        List<Form> formList = formRepository.findAllByUserAndFormType(user, documentType);

        return formList.stream()
                .map(GetFormRes::of)
                .toList();
    }
}
