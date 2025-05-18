package Sprout_Squad.EyeOn.domain.form.web.dto;

import java.util.List;

public record GetFieldRes(
        String doctype,
        List<String> tokens,
        List<List<Double>> bboxes,
        List<String> labels
) {
    public static GetFieldRes of(String doctype, List<String> tokens, List<List<Double>> bboxes, List<String> labels) {
        return new GetFieldRes(doctype, tokens, bboxes, labels);
    }
}
