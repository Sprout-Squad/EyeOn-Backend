package Sprout_Squad.EyeOn.domain.document.web.controller;

import Sprout_Squad.EyeOn.domain.document.service.DocumentService;
import Sprout_Squad.EyeOn.domain.document.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import Sprout_Squad.EyeOn.global.flask.service.FlaskService;
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
    private final FlaskService flaskService;
    private final PdfService pdfService;
    private final S3Service s3Service;

    // 문서 업로드
    @PostMapping
    public ResponseEntity<SuccessResponse<UploadDocumentRes>> uploadDocument(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestPart("file") MultipartFile file) throws IOException {
        UploadDocumentRes uploadDocumentRes = documentService.uploadDocument(userPrincipal, file);
        return ResponseEntity.ok(SuccessResponse.from(uploadDocumentRes));
    }

    // 문서 상세 조회
    @GetMapping("/{documentId}/detail")
    public ResponseEntity<SuccessResponse<GetDocumentRes>> getDocumentDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long documentId) {
        GetDocumentRes getDocumentRes = documentService.getOneDocument(userPrincipal, documentId);
        return ResponseEntity.ok(SuccessResponse.from(getDocumentRes));
    }

    // 문서 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<SuccessResponse<List<GetDocumentRes>>> getDocumentList(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<GetDocumentRes> getDocumentResList = documentService.getAllDocuments(userPrincipal);
        return ResponseEntity.ok(SuccessResponse.from(getDocumentResList));
    }

    // 문서 요약
    @GetMapping("/{documentId}/summary")
    public ResponseEntity<SuccessResponse<GetSummaryRes>> getDocumentSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long documentId){
        GetSummaryRes getSummaryRes = documentService.getSummary(userPrincipal, documentId);
        return ResponseEntity.ok(SuccessResponse.from(getSummaryRes));
    }

    // 문서 작성
    @PostMapping("/{formId}/write")
    public ResponseEntity<SuccessResponse<WriteDocsRes>> writeDocument(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long formId, @RequestBody WriteDocsReqWrapper writeDocsReqWrapper) throws IOException {
        WriteDocsRes writeDocsRes = documentService.writeDocument(userPrincipal, formId, writeDocsReqWrapper.data());
        return ResponseEntity.ok(SuccessResponse.from(writeDocsRes));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<SuccessResponse<WriteDocsRes>> rewriteDocument(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long documentId, @RequestBody ModifyDocumentReqWrapper modifyDocumentReqWrapper) throws IOException {
        WriteDocsRes writeDocsRes = documentService.rewriteDocument(userPrincipal, documentId, modifyDocumentReqWrapper);
        return ResponseEntity.ok(SuccessResponse.from(writeDocsRes));
    }



    // 문서 조언
    @GetMapping("/{documentId}/advice")
    public ResponseEntity<SuccessResponse<?>> getDocumentAdvice(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long documentId) throws IOException {
        List<GetAdviceRes> getAdviceResList=documentService.getAdvice(userPrincipal, documentId);
        return ResponseEntity.ok(SuccessResponse.from(getAdviceResList));
    }
}