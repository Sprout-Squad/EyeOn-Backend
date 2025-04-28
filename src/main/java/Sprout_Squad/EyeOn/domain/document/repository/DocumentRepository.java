package Sprout_Squad.EyeOn.domain.document.repository;

import Sprout_Squad.EyeOn.domain.document.entity.Document;
import Sprout_Squad.EyeOn.domain.document.exception.DocumentNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    default Document getDocumentsByDocumentId(Long documentId) {
        return findById(documentId).orElseThrow(DocumentNotFoundException::new);
    }
}
