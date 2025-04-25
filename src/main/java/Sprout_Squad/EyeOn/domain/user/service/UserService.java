package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    SignUpRes signUp(SignUpReq signUpReq);
    void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq, @AuthenticationPrincipal UserPrincipal userPrincipal);
    GetUserInfoRes getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal);
    GetResidentInfoRes getResidentInfo(MultipartFile multipartFile);
}
