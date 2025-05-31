package Sprout_Squad.EyeOn.global.flask.dto;

import java.util.List;

public record GetFieldForWriteRes(
        String field,
        String targetField,
        int index,
        List<Double> bbox,
        String displayName,
        String value
) {
}
