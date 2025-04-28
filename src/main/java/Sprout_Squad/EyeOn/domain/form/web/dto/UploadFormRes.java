package Sprout_Squad.EyeOn.domain.form.web.dto;

import Sprout_Squad.EyeOn.domain.form.entity.Form;

public record UploadFormRes(
        Long formId,
        String name,
        long formSize,
        String imageUrl
) {
    public static UploadFormRes of(Form form, long size) {
        return new UploadFormRes(form.getId(), form.getName(), size, form.getImageUrl());
    }
}
