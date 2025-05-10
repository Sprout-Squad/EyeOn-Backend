package Sprout_Squad.EyeOn.domain.document.web.dto;

public record GetSummaryRes(
        String summaryText,

        String pdfFileUrl
) {
    public static GetSummaryRes of(String summaryText, String pdfFileUrl) {
        return new GetSummaryRes(summaryText, pdfFileUrl);
    }
}
