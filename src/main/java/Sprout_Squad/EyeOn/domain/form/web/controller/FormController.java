package Sprout_Squad.EyeOn.domain.form.web.controller;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.service.FormService;
import Sprout_Squad.EyeOn.domain.form.web.dto.GetFormRes;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/form")
public class FormController {
    private final FormService formService;

    @GetMapping("/detail")
    public SuccessResponse<GetFormRes> getFormDetail(@RequestParam Long formId) {
        GetFormRes getFormRes = formService.getOneForm(formId);
        return SuccessResponse.of(getFormRes);
    }

    @GetMapping("/list")
    public SuccessResponse<List<GetFormRes>> getFormList(@RequestParam DocumentType documentType) {
        List<GetFormRes> getFormResList = formService.getAllFormsByType(documentType);
        return SuccessResponse.of(getFormResList);
    }
}
