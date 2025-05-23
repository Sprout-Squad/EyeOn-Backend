package Sprout_Squad.EyeOn.domain.document.web.controller;

import Sprout_Squad.EyeOn.domain.document.service.DocumentService;
import Sprout_Squad.EyeOn.domain.document.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<SuccessResponse<UploadDocumentRes>> uploadForm(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                             @RequestPart("file") MultipartFile file) throws IOException {
        UploadDocumentRes uploadDocumentRes = documentService.uploadDocument(userPrincipal, file);
        return ResponseEntity.ok(SuccessResponse.from(uploadDocumentRes));
    }

    @GetMapping("/{documentId}/detail")
    public ResponseEntity<SuccessResponse<GetDocumentRes>> getDocumentDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long documentId) {
        GetDocumentRes getDocumentRes = documentService.getOneDocument(userPrincipal, documentId);
        return ResponseEntity.ok(SuccessResponse.from(getDocumentRes));
    }

    @GetMapping("/list")
    public ResponseEntity<SuccessResponse<List<GetDocumentRes>>> getDocumentList(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<GetDocumentRes> getDocumentResList = documentService.getAllDocuments(userPrincipal);
        return ResponseEntity.ok(SuccessResponse.from(getDocumentResList));
    }

    @GetMapping("/{documentId}/summary")
    public ResponseEntity<SuccessResponse<GetSummaryRes>> getDocumentSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long documentId){
        GetSummaryRes getSummaryRes = documentService.getSummary(userPrincipal, documentId);
        return ResponseEntity.ok(SuccessResponse.from(getSummaryRes));
    }

    @PostMapping("/{formId}/write")
    public ResponseEntity<SuccessResponse<WriteDocsRes>> writeDocument(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long formId, @RequestBody WriteDocsReqWrapper writeDocsReqWrapper) throws IOException {
        WriteDocsRes writeDocsRes = documentService.writeDocument(userPrincipal, formId, writeDocsReqWrapper.data());
        return ResponseEntity.ok(SuccessResponse.from(writeDocsRes));
    }
}