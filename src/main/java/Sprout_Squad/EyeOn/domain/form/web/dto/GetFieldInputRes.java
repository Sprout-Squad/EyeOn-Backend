package Sprout_Squad.EyeOn.domain.form.web.dto;

import java.util.List;

public record GetFieldInputRes(
        List<FieldInputRes> isExist,
        List<FieldInputRes> notExist
) {}