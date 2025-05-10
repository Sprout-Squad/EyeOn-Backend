package Sprout_Squad.EyeOn.domain.form.web.dto;

import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;

import java.time.LocalDateTime;

public record GetFormRes(
        Long formId,
        String name,
        LocalDateTime createdAt,
        FormType formType,
        long formSize,
        String formUrl
) {
    public static GetFormRes of(Form form, long formSize){
        return new GetFormRes(
                form.getId(),
                form.getName(),
                form.getCreatedAt(),
                form.getFormType(),
                formSize,
                form.getFormUrl()
        );
    }
}
