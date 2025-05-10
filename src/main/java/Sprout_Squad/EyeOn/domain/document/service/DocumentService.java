package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.GetDocumentRes;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetSummaryRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;

import java.util.List;

public interface DocumentService {
    GetDocumentRes getOneDocument(UserPrincipal userPrincipal, Long id);
    List<GetDocumentRes> getAllDocuments(UserPrincipal userPrincipal);
    GetSummaryRes getSummary(UserPrincipal userPrincipal, Long id);
}
