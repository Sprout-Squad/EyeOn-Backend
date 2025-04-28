package Sprout_Squad.EyeOn.domain.document.repository;

import Sprout_Squad.EyeOn.domain.document.entity.Document;
import Sprout_Squad.EyeOn.domain.document.exception.DocumentNotFoundException;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    default Document getDocumentsByDocumentId(Long documentId) {
        return findById(documentId).orElseThrow(DocumentNotFoundException::new);
    }

    List<Document> findAllByUser(User user);
}
