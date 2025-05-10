package Sprout_Squad.EyeOn.domain.document.web.dto;

public record GetSummaryRes(
        String summaryText,

        byte[] summaryPdf
) {
    public static GetSummaryRes of(String summaryText, byte[] summaryPdf) {
        return new GetSummaryRes(summaryText, summaryPdf);
    }
}
