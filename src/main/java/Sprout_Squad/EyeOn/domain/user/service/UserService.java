package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.GetUserInfoRes;
import Sprout_Squad.EyeOn.domain.user.web.dto.ModifyUserInfoReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;

public interface UserService {
    SignUpRes signUp(SignUpReq signUpReq);
    void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq);
    GetUserInfoRes getUserInfo();
}
