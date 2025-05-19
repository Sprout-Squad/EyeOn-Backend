package Sprout_Squad.EyeOn.domain.form.web.dto;

import java.util.List;

public record GetFieldRes(
        String field,
        String targetField,
        int index,
        List<Double> bbox,
        String displayName,
        String value
) {
}
