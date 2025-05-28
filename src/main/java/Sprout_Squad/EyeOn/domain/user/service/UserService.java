package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    SignUpRes signUp(SignUpReq signUpReq);
    ModifyUserInfoRes modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq, MultipartFile file, @AuthenticationPrincipal UserPrincipal userPrincipal) throws IOException;
    GetUserInfoRes getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal);
    GetResidentInfoRes getResidentInfo(MultipartFile multipartFile);
}
