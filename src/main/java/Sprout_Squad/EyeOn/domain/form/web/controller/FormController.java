package Sprout_Squad.EyeOn.domain.form.web.controller;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.service.FormService;
import Sprout_Squad.EyeOn.global.flask.dto.GetFieldForWriteRes;
import Sprout_Squad.EyeOn.global.flask.dto.GetModelRes;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.form.web.dto.UploadFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
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
@RequestMapping("/api/form")
public class FormController {
    private final FormService formService;
    private final FlaskService flaskService;

    @PostMapping
    public ResponseEntity<SuccessResponse<UploadFormRes>> uploadForm(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                     @RequestPart("file") MultipartFile file) throws IOException {
        UploadFormRes uploadFormRes = formService.uploadForm(userPrincipal, file);
        return ResponseEntity.ok(SuccessResponse.from(uploadFormRes));
    }

    @PostMapping("/analyze/field")
    public ResponseEntity<SuccessResponse<List<GetFieldForWriteRes>>> getFormField(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                                   @RequestPart("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();

        GetModelRes getModelRes = flaskService.getResFromModelForWrite(file, fileName);
        List<GetFieldForWriteRes> getFieldForWriteResList = flaskService.getFieldForWrite(getModelRes, userPrincipal);

        return ResponseEntity.ok(SuccessResponse.from(getFieldForWriteResList));
    }

    @GetMapping("/{formId}/detail")
    public ResponseEntity<SuccessResponse<GetFormRes>> getFormDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long formId) {
        GetFormRes getFormRes = formService.getOneForm(userPrincipal, formId);
        return ResponseEntity.ok(SuccessResponse.from(getFormRes));
    }

    @GetMapping("/list")
    public ResponseEntity<SuccessResponse<List<GetFormRes>>> getFormList(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam FormType formType) {
        List<GetFormRes> getFormResList = formService.getAllFormsByType(userPrincipal, formType);
        return ResponseEntity.ok(SuccessResponse.from(getFormResList));
    }
}