package Sprout_Squad.EyeOn.global.flask.dto;

import java.util.List;

public record GetModelResForModify(
        String doctype,
        List<String> tokens,
        List<List<Double>> bboxes,
        List<String> labels,
        List<Integer> indices, // bboxes와 일대일 대응되는 인덱스
        List<String> mergedTokens

) {
    public static GetModelResForModify of(String doctype, List<String> tokens, List<List<Double>> bboxes, List<String> labels, List<String> mergedTokens) {
        // index 리스트 생성
        List<Integer> indices = new java.util.ArrayList<>();
        for (int i = 0; i < bboxes.size(); i++) {
            indices.add(i); // bbox index 기준
        }

        return new GetModelResForModify(doctype, tokens, bboxes, labels, indices, mergedTokens);
    }
}
