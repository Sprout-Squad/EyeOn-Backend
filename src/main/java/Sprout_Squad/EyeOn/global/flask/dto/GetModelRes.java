package Sprout_Squad.EyeOn.global.flask.dto;

import java.util.List;

public record GetModelRes(
        String doctype,
        List<String> tokens,
        List<List<Double>> bboxes,
        List<String> labels
) {
    public static GetModelRes of(String doctype, List<String> tokens, List<List<Double>> bboxes, List<String> labels) {
        return new GetModelRes(doctype, tokens, bboxes, labels);
    }
}