package Sprout_Squad.EyeOn.domain.document.service;

import Sprout_Squad.EyeOn.domain.document.entity.Document;
import Sprout_Squad.EyeOn.domain.document.repository.DocumentRepository;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetDocumentRes;
import Sprout_Squad.EyeOn.domain.document.web.dto.GetSummaryRes;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.CanNotAccessException;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.external.service.OpenAiService;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final OpenAiService openAiService;
    private final PdfService pdfService;

    /**
     * 문서 양식 하나 상세 조회
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
     *
     * @param userPrincipal
     * @return
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
     * 요약 생성
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


}
