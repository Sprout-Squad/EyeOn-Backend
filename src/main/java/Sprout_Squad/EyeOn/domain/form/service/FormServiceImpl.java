package Sprout_Squad.EyeOn.domain.form.service;

import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.repository.FormRepository;
import Sprout_Squad.EyeOn.domain.form.web.dto.*;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.global.auth.exception.CanNotAccessException;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.converter.ImgConverter;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import Sprout_Squad.EyeOn.global.flask.exception.GetLabelFailedException;
import Sprout_Squad.EyeOn.global.flask.service.FlaskService;
import Sprout_Squad.EyeOn.global.external.exception.UnsupportedFileTypeException;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PdfService pdfService;
    private final FlaskService flaskService;

    /**
     * 양식 판별
     */
    @Override
    public FormType getFormType(MultipartFile file, String fileName) {
        String type = flaskService.detectType(file, fileName);
        return FormType.from(type);
    }

    /**
     * 양식 필드 분석
     */
    @Override
    public GetModelRes getResFromModel(MultipartFile file, String fileName){
        try {
            String fileToBase64 = ImgConverter.toBase64(file);
            String fileExtension = pdfService.getFileExtension(fileName);

            // 플라스크 요청
            String jsonRes = flaskService.getLabel(fileToBase64, fileExtension);

            // jsonRes를 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = mapper.readValue(jsonRes, Map.class);

            // tokens: 문서에서 추출된 텍스트 조각
            List<String> tokens = (List<String>) resultMap.get("tokens");
//            System.out.println("문서 tokens: "+ tokens);

            // labels: 각 토큰의 labels
            List<String> labels = (List<String>) resultMap.get("labels");
//            System.out.println("문서 labels: "+labels);

            // doctype: 문서 유형
            String doctype = (String) resultMap.get("doctype");

            // rawBboxes: 각 토큰의 bounding box 좌표 정보
            List<List<?>> rawBboxes = (List<List<?>>) resultMap.get("bboxes");

            // bboxes: 각 토큰의 bounding box 좌표 정보
            List<List<Double>> bboxes = new ArrayList<>();

            // List<?> 형태로 파싱된 좌표 데이터를 List<Double>로 변환
            for (List<?> box : rawBboxes) {
                List<Double> convertedBox = box.stream()
                        .map(val -> ((Number) val).doubleValue()) // JSON 파싱 결과가 Number 타입 -> 형변환 필요
                        .toList();
                bboxes.add(convertedBox);
            }
            return GetModelRes.of(doctype, tokens, bboxes, labels);
        } catch (Exception e){ throw new GetLabelFailedException();}

    }

    /**
     * 사용자가 양식 업로드 (pdf, png)
     */
    @Override
    @Transactional
    public UploadFormRes uploadForm(UserPrincipal userPrincipal, MultipartFile file) throws IOException {
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
        FormType formType = getFormType(file, fileName);

        Form form = Form.toEntity(file, fileUrl, formType, user);
        formRepository.save(form);
        return UploadFormRes.of(form, s3Service.getSize(fileUrl));
    }

    /**
     * 양식 하나 상세 조회
     */
    @Override
    public GetFormRes getOneForm(UserPrincipal userPrincipal, Long formId) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        // 양식이 존재하지 않을 경우 ->  FormNotFoundException
        Form form = formRepository.getFormByFormId(formId);

        // 사용자의 양식이 아닐 경우 -> CanNotAccessException
        if(form.getUser() != user) throw new CanNotAccessException();

        return GetFormRes.of(form, s3Service.getSize(form.getFormUrl()));
    }

    /**
     * 타입 별 양식 모두 조회
     */
    @Override
    public List<GetFormRes> getAllFormsByType(UserPrincipal userPrincipal, FormType formType) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        List<Form> formList = formRepository.findAllByUserAndFormType(user, formType);

        return formList.stream()
                .map(form -> {
                    long formSize = s3Service.getSize(form.getFormUrl());
                    return GetFormRes.of(form, formSize);
                })
                .toList();
    }
}
