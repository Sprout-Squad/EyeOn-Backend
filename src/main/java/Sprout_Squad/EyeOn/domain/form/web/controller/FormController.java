package Sprout_Squad.EyeOn.domain.form.web.controller;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.service.FormService;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/form")
public class FormController {
    private final FormService formService;

    @GetMapping("/detail")
    public SuccessResponse<GetFormRes> getFormDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long formId) {
        GetFormRes getFormRes = formService.getOneForm(userPrincipal, formId);
        return SuccessResponse.of(getFormRes);
    }

    @GetMapping("/list")
    public SuccessResponse<List<GetFormRes>> getFormList(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam DocumentType documentType) {
        List<GetFormRes> getFormResList = formService.getAllFormsByType(userPrincipal, documentType);
        return SuccessResponse.of(getFormResList);
    }
}
