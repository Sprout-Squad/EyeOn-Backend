package Sprout_Squad.EyeOn.domain.user.web.controller;

import Sprout_Squad.EyeOn.domain.user.service.UserService;
import Sprout_Squad.EyeOn.domain.user.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.domain.user.web.dto.ModifyUserInfoReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/kakao/signUp")
    public SuccessResponse<SignUpRes> signUp(@RequestBody @Valid SignUpReq signUpReq) {
        SignUpRes signUpRes = userService.signUp(signUpReq);
        return SuccessResponse.of(signUpRes);
    }

    @PutMapping("/modify")
    public SuccessResponse<Void> modifyUserInfo(
            @RequestBody @Valid ModifyUserInfoReq modifyUserInfoReq, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.modifyUserInfo(modifyUserInfoReq, userPrincipal);
        return SuccessResponse.empty();
    }

    @GetMapping("/info")
    public SuccessResponse<GetUserInfoRes> getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return SuccessResponse.of(userService.getUserInfo(userPrincipal));
    }
}
