package Sprout_Squad.EyeOn.domain.form.web.dto;

public record GetFieldRes(
        String field,
        String targetField,
        int index,
        String displayName,
        String value
) {
}
