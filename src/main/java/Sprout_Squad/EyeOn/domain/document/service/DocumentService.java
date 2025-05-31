package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    GetDocumentRes getOneDocument(UserPrincipal userPrincipal, Long id);
    List<GetDocumentRes> getAllDocuments(UserPrincipal userPrincipal);
    GetSummaryRes getSummary(UserPrincipal userPrincipal, Long id);
    WriteDocsRes writeDocument(UserPrincipal userPrincipal, Long formId, List<WriteDocsReq> writeDocsReqList) throws IOException;
    UploadDocumentRes uploadDocument(UserPrincipal userPrincipal, MultipartFile file) throws IOException;
    WriteDocsRes rewriteDocument(UserPrincipal userPrincipal, Long documentId, ModifyDocumentReqWrapper modifyDocumentReqWrapper) throws IOException;
    List<GetAdviceRes> getAdvice(UserPrincipal userPrincipal, Long id) throws IOException;

}
