package Sprout_Squad.EyeOn.domain.form.web.dto;

import java.util.List;

public record GetFormFieldRes(
        String doctype,
        List<String> tokens,
        List<List<Double>> bboxes,
        List<String> labels
) {
    public static GetFormFieldRes of(String doctype, List<String> tokens, List<List<Double>> bboxes, List<String> labels) {
        return new GetFormFieldRes(doctype, tokens, bboxes, labels);
    }
}
