package Sprout_Squad.EyeOn.domain.document.web.dto;

import Sprout_Squad.EyeOn.domain.document.entity.Document;

import java.time.LocalDateTime;

public record WriteDocsRes(
        Long documentId,
        String documentName,
        LocalDateTime createdDate,
        String imageUrl,
        String pdfUrl
) {
    public static WriteDocsRes from(Document document) {
        return new WriteDocsRes(document.getId(), document.getDocumentName(), document.getCreatedAt(), document.getDocumentImageUrl(),
                document.getDocumentUrl());
    }
}
