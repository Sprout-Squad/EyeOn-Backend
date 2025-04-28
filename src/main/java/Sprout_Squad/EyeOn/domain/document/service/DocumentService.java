package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.GetDocumentRes;

public interface DocumentService {
    GetDocumentRes getOneDocument(Long id);
}
