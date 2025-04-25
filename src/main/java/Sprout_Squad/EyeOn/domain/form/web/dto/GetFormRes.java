package Sprout_Squad.EyeOn.domain.form.web.dto;

import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;

import java.time.LocalDateTime;

public record GetFormRes(
        Long formId,
        String name,
        LocalDateTime createdAt,
        FormType formType,
        String imageUrl
) {
    public static GetFormRes of(Form form){
        return new GetFormRes(
                form.getId(),
                form.getName(),
                form.getCreatedAt(),
                form.getFormType(),
                form.getImageUrl()
        );
    }
}
