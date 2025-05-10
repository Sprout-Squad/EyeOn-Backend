package Sprout_Squad.EyeOn.domain.document.web.dto;

import Sprout_Squad.EyeOn.domain.document.entity.Document;
import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;

import java.time.LocalDateTime;

public record GetDocumentRes(
        Long documentId,
        String name,
        LocalDateTime createdAt,
        DocumentType documentType,
        long documentSize,
        String documentUrl
) {
    public static GetDocumentRes of(Document document, long fileSize) {
        return new GetDocumentRes(
                document.getId(),
                document.getDocumentName(),
                document.getCreatedAt(),
                document.getDocumentType(),
                fileSize,
                document.getDocumentUrl());
    }
}
