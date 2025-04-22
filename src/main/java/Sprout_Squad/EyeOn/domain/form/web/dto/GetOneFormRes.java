package Sprout_Squad.EyeOn.domain.form.web.dto;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.user.web.dto.GetUserInfoRes;

import java.time.LocalDateTime;

public record GetOneFormRes(
        String name,
        LocalDateTime createdAt,
        DocumentType formType,
        String imageUrl
) {
    public static GetOneFormRes of(Form form){
        return new GetOneFormRes(
                form.getName(),
                form.getCreatedAt(),
                form.getFormType(),
                form.getImageUrl()
        );
    }
}
