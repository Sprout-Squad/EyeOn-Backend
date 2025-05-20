package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.entity.Document;
import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.document.repository.DocumentRepository;
import Sprout_Squad.EyeOn.domain.document.web.dto.*;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.repository.FormRepository;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.CanNotAccessException;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.external.service.OpenAiService;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final OpenAiService openAiService;
    private final PdfService pdfService;
    private final FormRepository formRepository;

    /**
     * 사용자의 문서 하나 상세 조회
     */
    @Override
    public GetDocumentRes getOneDocument(UserPrincipal userPrincipal, Long id) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 문서가 존재하지 않을 경우 ->  DocumentNotFoundException
        Document document = documentRepository.getDocumentsByDocumentId(id);

        // 사용자의 문서가 아닐 경우 -> CanNotAccessException
        if(document.getUser() != user) throw new CanNotAccessException();

        return GetDocumentRes.of(document, s3Service.getSize(document.getDocumentUrl()));
    }

    /**
     * 사용자의 모든 문서 조회
     */
    @Override
    public List<GetDocumentRes> getAllDocuments(UserPrincipal userPrincipal) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        List<Document> documentList = documentRepository.findAllByUser(user);

        return documentList.stream()
                .map(document ->{
                    long documentSize = s3Service.getSize(document.getDocumentUrl());
                    return GetDocumentRes.of(document, documentSize);
                })
                .toList();
    }

    /**
     * 문서 요약
     */
    @Override
    public GetSummaryRes getSummary(UserPrincipal userPrincipal, Long id) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 문서가 존재하지 않을 경우 ->  DocumentNotFoundException
        Document document = documentRepository.getDocumentsByDocumentId(id);

        // 사용자의 문서가 아닐 경우 -> CanNotAccessException
        if(document.getUser() != user) throw new CanNotAccessException();

        // 1. GPT 요약 요청
        String summaryText = openAiService.getSummaryFromOpenAi(document.getDocumentImageUrl());

        // 2. 텍스트 -> PDF 변환하여 저장
        String summaryFileUrl = pdfService.textToPdf(summaryText);

        // 3. DTO로 반환
        return GetSummaryRes.of(summaryText, summaryFileUrl);
    }

    /**
     * 문서 작성
     */
    @Override
    @Transactional
    public WriteDocsRes writeDocument(UserPrincipal userPrincipal, Long formId, List<WriteDocsReq> writeDocsReqList) throws IOException {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 양식이 존재하지 않을 경우 -> FormNotFoundException
        Form form = formRepository.getFormByFormId(formId);

        // 사용자의 양식이 아닐 경우 -> CanNotAccessException
        if(form.getUser() != user) throw new CanNotAccessException();

        // 이미지 url
        String imgUrl = pdfService.fillImageFromS3(form.getFormUrl(), writeDocsReqList);

        // pdf url
        byte[] imgToPdf = pdfService.convertImageToPdf(s3Service.downloadFile(imgUrl));

        // S3에 pdf 업로드
        String fileName = s3Service.generatePdfFileName();
        String pdfUrl = s3Service.uploadPdfBytes(fileName, imgToPdf);

        DocumentType documentType = DocumentType.valueOf(form.getFormType().name());

        Document document = Document.toEntity(documentType, imgUrl, pdfUrl, form, user);
        documentRepository.save(document);

        return WriteDocsRes.from(document);

    }


}
