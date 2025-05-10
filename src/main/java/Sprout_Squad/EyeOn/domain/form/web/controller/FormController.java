package Sprout_Squad.EyeOn.domain.form.web.controller;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.form.service.FormService;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.domain.form.web.dto.UploadFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public SuccessResponse<UploadFormRes> uploadForm(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @RequestPart("file") MultipartFile file,
                                      @RequestParam("formType") FormType formType) throws IOException {
        /**
         * 지금은 요청에 formType 받아 넘기지만 추후에는 플라스크 서버로부터, FormType 판별 받아서 넘겨야 함
         */
        UploadFormRes uploadFormRes = formService.uploadForm(userPrincipal, file, formType);
        return SuccessResponse.from(uploadFormRes);
    }

    @GetMapping("/detail")
    public SuccessResponse<GetFormRes> getFormDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long formId) {
        GetFormRes getFormRes = formService.getOneForm(userPrincipal, formId);
        return SuccessResponse.from(getFormRes);
    }

    @GetMapping("/list")
    public SuccessResponse<List<GetFormRes>> getFormList(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam FormType formType) {
        List<GetFormRes> getFormResList = formService.getAllFormsByType(userPrincipal, formType);
        return SuccessResponse.from(getFormResList);
    }
}
