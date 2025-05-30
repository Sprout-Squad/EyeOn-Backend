package Sprout_Squad.EyeOn.domain.form.repository;

import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.exception.FormNotFoundException;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findAllByUserAndFormType(User user, FormType formType);

    default Form getFormByFormId(Long id){
        return findById(id).orElseThrow(FormNotFoundException::new);
    }
}
