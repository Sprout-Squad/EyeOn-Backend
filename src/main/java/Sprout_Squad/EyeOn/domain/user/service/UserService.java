package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

public interface UserService {
    SignUpRes signUp(SignUpReq signUpReq);
    void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq);
}
