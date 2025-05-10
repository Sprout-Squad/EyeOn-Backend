package Sprout_Squad.EyeOn.domain.document.web.controller;

import Sprout_Squad.EyeOn.domain.document.service.DocumentService;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetDocumentRes;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetSummaryRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("/detail")
    public SuccessResponse<GetDocumentRes> getDocumentDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long documentId) {
        GetDocumentRes getDocumentRes = documentService.getOneDocument(userPrincipal, documentId);
        return SuccessResponse.from(getDocumentRes);
    }

    @GetMapping("/list")
    public SuccessResponse<List<GetDocumentRes>> getDocumentList(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<GetDocumentRes> getDocumentResList = documentService.getAllDocuments(userPrincipal);
        return SuccessResponse.from(getDocumentResList);
    }

    @GetMapping("/summary")
    public SuccessResponse<GetSummaryRes> getDocumentSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long documentId){
        GetSummaryRes getSummaryRes = documentService.getSummary(userPrincipal, documentId);
        return SuccessResponse.from(getSummaryRes);
    }
}