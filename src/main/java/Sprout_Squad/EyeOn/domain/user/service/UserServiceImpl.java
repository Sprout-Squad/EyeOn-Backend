package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.exception.UserAlreadyExistException;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.web.dto.LoginReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.LoginRes;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void signUp(SignUpReq signUpReq) {
        User userByResident = userRepository.findByResidentNumber(signUpReq.residentNumber());
        if(userByResident != null) throw new UserAlreadyExistException();

        User userByPhone = userRepository.findByPhoneNumber(signUpReq.phoneNumber());
        if(userByPhone != null) throw new UserAlreadyExistException();

        User newUser = User.toEntity(signUpReq);
        userRepository.save(newUser);
    }

    @Override
    public LoginRes login(LoginReq loginReq) {

        return null;
    }
}
