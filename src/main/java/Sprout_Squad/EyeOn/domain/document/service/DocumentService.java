package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.GetDocumentRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;

public interface DocumentService {
    GetDocumentRes getOneDocument(UserPrincipal userPrincipal, Long id);
}
