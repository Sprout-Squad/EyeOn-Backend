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
import Sprout_Squad.EyeOn.global.flask.exception.GetLabelFailedException;
import Sprout_Squad.EyeOn.global.flask.exception.TypeDetectedFiledException;
import Sprout_Squad.EyeOn.global.flask.mapper.FieldLabelMapper;
import Sprout_Squad.EyeOn.global.flask.service.FlaskService;
import Sprout_Squad.EyeOn.global.external.exception.UnsupportedFileTypeException;
import Sprout_Squad.EyeOn.global.external.service.PdfService;
import Sprout_Squad.EyeOn.global.external.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private final FieldLabelMapper fieldLabelMapper;

    /**
     * 양식 판별
     */
    @Override
    public FormType getFormType(MultipartFile file, String fileName) {
        try {
            String fileToBase64 = ImgConverter.toBase64(file);

            String fileExtension = pdfService.getFileExtension(fileName);
            String type = flaskService.detectType(fileToBase64, fileExtension);

            return FormType.from(type);

        } catch (Exception e){ throw new TypeDetectedFiledException(); }
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
            System.out.println("문서 tokens: "+ tokens);

            // labels: 각 토큰의 labels
            List<String> labels = (List<String>) resultMap.get("labels");
            System.out.println("문서 labels: "+labels);

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
     * db에 있는 정보를 채워서 반환
     */
    public List<GetFieldRes> getField(GetModelRes getModelRes, UserPrincipal userPrincipal){
        System.out.println("GetModelRes: " +getModelRes);

        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());

        List<String> tokens = getModelRes.tokens();
        List<String> labels = getModelRes.labels();
        List<List<Double>> bboxes = getModelRes.bboxes();

        String docType = getModelRes.doctype();

        List<GetFieldRes> userInputs = new ArrayList<>();

        // context별 전체 label map 불러오기
        Map<String, Map<String, String>> contextLabelMap = fieldLabelMapper.getContextualLabels();

        // 현재 문서 타입에 해당하는 label 맵
        Map<String, String> labelMap = contextLabelMap.getOrDefault(docType, Collections.emptyMap());
        Map<String, String> commonMap = contextLabelMap.getOrDefault("common", Collections.emptyMap());

        for (int i = 0; i < tokens.size(); i++) {
            if ("[BLANK]".equals(tokens.get(i))) {
                String label = labels.get(i);

                if (label.endsWith("-FIELD")) { // label이 -FIELD 로 끝나면
                    String baseLabel = label.replace("-FIELD", "");

                    // displayName 우선순위: 1) 문서 타입 별 Map → 2) 공통 Map → 3) 기본값
                    String displayName = labelMap.getOrDefault(baseLabel,
                            commonMap.getOrDefault(baseLabel, baseLabel));

                    // 사용자 정보에서 가져올 수 있는 값 매핑
                    String value = switch (baseLabel) {
                        case "B-PERSONAL-NAME", "B-GRANTOR-NAME", "B-DELEGATE-NAME", "B-NAME", "B-SIGN-NAME" -> user.getName();
                        case "B-PERSONAL-RRN", "B-GRANTOR-RRN", "B-DELEGATE-RRN" -> user.getResidentNumber();
                        case "B-PERSONAL-PHONE", "B-GRANTOR-PHONE", "B-DELEGATE-PHONE" -> user.getPhoneNumber();
                        case "B-PERSONAL-ADDR", "B-GRANTOR-ADDR", "B-DELEGATE-ADDR" -> user.getAddress();
                        case "B-PERSONAL-EMAIL" -> user.getEmail();
                        default -> null; // 나머지는 프론트로부터 값 받기
                    };

                    List<Double> bbox = bboxes.get(i);

                    userInputs.add(new GetFieldRes(
                            baseLabel,
                            label,
                            i,
                            bbox,
                            displayName,
                            value
                    ));
                }
            }
        }
        return userInputs;
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

        System.out.println("파일 확장자 : " + extension);
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

        System.out.println("fileName : " + fileName);
        System.out.println("fileUrl : " + fileUrl);

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
