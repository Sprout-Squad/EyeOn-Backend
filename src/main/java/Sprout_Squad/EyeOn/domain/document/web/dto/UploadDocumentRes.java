package Sprout_Squad.EyeOn.domain.document.web.dto;

import Sprout_Squad.EyeOn.domain.document.entity.Document;

public record UploadDocumentRes(
        Long documentId,
        String name,
        long documentSize,
        String documentUrl
) {
    public static UploadDocumentRes of(Document document, long size) {
        return new UploadDocumentRes(document.getId(), document.getDocumentName(), size, document.getDocumentUrl());
    }
}
