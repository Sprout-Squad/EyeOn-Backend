package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;

public interface UserService {
    void signUp(SignUpReq signUpReq);
}
