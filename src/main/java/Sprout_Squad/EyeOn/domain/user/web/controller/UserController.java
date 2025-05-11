package Sprout_Squad.EyeOn.domain.user.web.controller;

import Sprout_Squad.EyeOn.domain.user.service.UserService;
import Sprout_Squad.EyeOn.domain.user.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/kakao/signUp")
    public ResponseEntity<SuccessResponse<SignUpRes>> signUp(@RequestBody @Valid SignUpReq signUpReq) {
        SignUpRes signUpRes = userService.signUp(signUpReq);
        return ResponseEntity.ok(SuccessResponse.from(signUpRes));
    }

    @PutMapping("/modify")
    public ResponseEntity<SuccessResponse<Void>> modifyUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid ModifyUserInfoReq modifyUserInfoReq) {
        userService.modifyUserInfo(modifyUserInfoReq, userPrincipal);
        return ResponseEntity.ok(SuccessResponse.empty());
    }

    @GetMapping("/info")
    public ResponseEntity<SuccessResponse<GetUserInfoRes>> getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(SuccessResponse.from(userService.getUserInfo(userPrincipal)));
    }

    @PostMapping("/getResidentInfo")
    public ResponseEntity<SuccessResponse<GetResidentInfoRes>> getResidentInfo(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(SuccessResponse.from(userService.getResidentInfo(file)));
    }
}
