package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.domain.user.web.dto.ModifyUserInfoReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface UserService {
    SignUpRes signUp(SignUpReq signUpReq);
    void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq, @AuthenticationPrincipal UserPrincipal userPrincipal);
    GetUserInfoRes getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal);
}
