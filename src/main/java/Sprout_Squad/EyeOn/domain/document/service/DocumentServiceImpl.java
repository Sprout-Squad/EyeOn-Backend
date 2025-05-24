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
import Sprout_Squad.EyeOn.global.converter.ImgConverter;
import Sprout_Squad.EyeOn.global.external.exception.UnsupportedFileTypeException;
import Sprout_Squad.EyeOn.global.external.service.OpenAiService;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import Sprout_Squad.EyeOn.global.flask.dto.GetFieldForModifyRes;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelResForModify;
import Sprout_Squad.EyeOn.global.flask.mapper.FieldLabelMapper;
import Sprout_Squad.EyeOn.global.flask.service.FlaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    private final FlaskService flaskService;
    private final FieldLabelMapper fieldLabelMapper;

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
        int dotIndex = fileName.lastIndexOf('.');
        String nameOnly = (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;
        String pdfUrl = s3Service.uploadPdfBytes(fileName, imgToPdf);

        DocumentType documentType = DocumentType.valueOf(form.getFormType().name());

        Document document = Document.toEntity(documentType, imgUrl, pdfUrl, form, nameOnly, user);
        documentRepository.save(document);

        return WriteDocsRes.from(document);
    }

    /**
     * 문서 업로드
     */
    @Override
    @Transactional
    public UploadDocumentRes uploadDocument(UserPrincipal userPrincipal, MultipartFile file) throws IOException {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        String extension = pdfService.getFileExtension(file.getOriginalFilename()).toLowerCase();

        String fileUrl;
        String fileName;

        if (extension.equals("pdf")) {
            // PDF -> 이미지 변환
            byte[] pdfBytes = file.getBytes();
            fileUrl = pdfService.convertPdfToImage(pdfBytes);
            fileName = s3Service.extractKeyFromUrl(fileUrl); // 변환된 이미지의 S3 key
        } else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")) {
            fileName = s3Service.generateFileName(file);
            fileUrl = s3Service.uploadFile(fileName, file);
        } else {
            throw new UnsupportedFileTypeException();
        }

        // 플라스크 서버와 통신하여 파일 유형 받아옴
        DocumentType documentType = DocumentType.from(flaskService.detectType(file, fileName));

        Document document = Document.toEntity(documentType, fileUrl, fileUrl, null, fileName, user);
        documentRepository.save(document);
        return UploadDocumentRes.of(document, s3Service.getSize(fileUrl));
    }

    /**
     * 문서 수정
     */
    @Override
    @Transactional
    public WriteDocsRes rewriteDocument(UserPrincipal userPrincipal, Long documentId,
                                        ModifyDocumentReqWrapper modifyDocumentReqWrapper) throws IOException {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 문서가 존재하지 않을 경우 -> DocumentNotFoundException
        Document document = documentRepository.getDocumentsByDocumentId(documentId);

        // 사용자의 문서가 아닐 경우 -> CanNotAccessException
        if(document.getUser() != user) throw new CanNotAccessException();

        // 실제 문서를 가져옴
        InputStream file = s3Service.downloadFile(document.getDocumentImageUrl());

        // InputStream을 MultipartFile로 변환
        MultipartFile multipartFile = new MockMultipartFile(
                document.getDocumentName(),
                document.getDocumentName(),
                "application/octet-stream",
                file
        );


        String fileExt = pdfService.getFileExtension(multipartFile.getOriginalFilename());
        // 모델로부터 작성된 문서의 OCR 및 라벨링 결과를 가져옴
        String res = flaskService.getModifyLabelReq(ImgConverter.toBase64(multipartFile), fileExt);

        // 수정 요청과 비교하여 WritDocsReqList 구성
        WriteDocsReqWrapper writeDocsReqList = flaskService.getModifyRes(res, modifyDocumentReqWrapper);

        // 이미지 url (이미지 덮어쓰기 및 재작성)
        String imgUrl = pdfService.rewriteImg(document.getDocumentImageUrl(), writeDocsReqList.data());

        // pdf
        byte[] imgToPdf = pdfService.convertImageToPdf(s3Service.downloadFile(imgUrl));

        // S3에 pdf 업로드
        String fileName = s3Service.generatePdfFileName();
        String pdfUrl = s3Service.uploadPdfBytes(fileName, imgToPdf);

        document.modifyDocument(imgUrl, pdfUrl);
        return WriteDocsRes.from(document);
    }

    /**
     * 문서 조언
     */
    @Override
    public List<GetAdviceRes> getAdvice(UserPrincipal userPrincipal, Long id) throws IOException {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 문서가 존재하지 않을 경우 ->  DocumentNotFoundException
        Document document = documentRepository.getDocumentsByDocumentId(id);

        // 사용자의 문서가 아닐 경우 -> CanNotAccessException
        if(document.getUser() != user) throw new CanNotAccessException();
        InputStream file = s3Service.downloadFile(document.getDocumentImageUrl());

        MultipartFile multipartFile = new MockMultipartFile(
                document.getDocumentName(),
                document.getDocumentName(),
                "application/octet-stream",
                file
        );

        GetModelResForModify getModelResForModify = flaskService.getResFromModelForModify(multipartFile, document.getDocumentName());

        // 가공하기 좋은 형태로 변환
        List<GetFieldForModifyRes> getFieldForModifyResList = flaskService.getFieldForModify(getModelResForModify);
        System.out.println("반환: " + getFieldForModifyResList);
//        List<GetAdviceReq> adviceReqList = getFieldForModifyResList.stream()
//                .map(field -> new GetAdviceReq(field.index(), field.displayName(), field.value()))
//                .collect(Collectors.toList());
//
//        System.out.println("reqList" + adviceReqList);
//
//        // OpenAI에 분석 요청
//        String result = openAiService.getModifyAnalyzeFromOpenAi(adviceReqList, document.getDocumentType());


        ObjectMapper objectMapper = new ObjectMapper();
        // SON 배열의 각 요소를 GetAdviceRes 객체로 변환하여 리스트로 모음
//        return objectMapper.readValue(result, new TypeReference<List<GetAdviceRes>>() {});
        return null;
    }


}
