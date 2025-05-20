package Sprout_Squad.EyeOn.domain.document.web.dto;

import java.util.List;

public record WriteDocsReq(
        String field,
        String targetField,
        int index,
        List<Double> bbox,
        String displayName,
        String value
) {
}
