package Sprout_Squad.EyeOn.domain.user.web.controller;

import Sprout_Squad.EyeOn.domain.user.service.UserService;
import Sprout_Squad.EyeOn.domain.user.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/kakao/signUp")
    public SuccessResponse<SignUpRes> signUp(@RequestBody @Valid SignUpReq signUpReq) {
        SignUpRes signUpRes = userService.signUp(signUpReq);
        return SuccessResponse.from(signUpRes);
    }

    @PutMapping("/modify")
    public SuccessResponse<Void> modifyUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid ModifyUserInfoReq modifyUserInfoReq) {
        userService.modifyUserInfo(modifyUserInfoReq, userPrincipal);
        return SuccessResponse.empty();
    }

    @GetMapping("/info")
    public SuccessResponse<GetUserInfoRes> getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return SuccessResponse.from(userService.getUserInfo(userPrincipal));
    }

    @PostMapping("/getResidentInfo")
    public SuccessResponse<GetResidentInfoRes> getResidentInfo(@RequestParam("file") MultipartFile file) {
        return SuccessResponse.from(userService.getResidentInfo(file));
    }
}
