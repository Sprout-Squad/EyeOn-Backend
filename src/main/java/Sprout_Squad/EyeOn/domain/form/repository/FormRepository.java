package Sprout_Squad.EyeOn.domain.form.repository;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findAllByUserAndFormType(User user, DocumentType formType);
    Optional<Form> findByFormId(Long formId);
}
