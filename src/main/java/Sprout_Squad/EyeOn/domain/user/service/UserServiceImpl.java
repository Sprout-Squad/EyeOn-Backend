package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.entity.enums.Gender;
import Sprout_Squad.EyeOn.domain.user.exception.UserAlreadyExistException;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.web.dto.ModifyUserInfoReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import Sprout_Squad.EyeOn.global.auth.jwt.AuthenticationUserUtils;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Override
    @Transactional
    public SignUpRes signUp(SignUpReq signUpReq) {
        Optional<User> userByKakaoId = userRepository.findByKakaoId(signUpReq.kakaoId());
        if (userByKakaoId.isPresent()) throw new UserAlreadyExistException();

        Optional<User> userByResident = userRepository.findByResidentNumber(signUpReq.residentNumber());
        if(userByResident.isPresent()) throw new UserAlreadyExistException();

        char genderDigit = signUpReq.residentNumber().charAt(7);
        Gender gender = (genderDigit=='1'||genderDigit=='2')?Gender.MALE:Gender.FEMALE;

        Optional<User> userByPhone = userRepository.findByPhoneNumber(signUpReq.phoneNumber());
        if(userByPhone.isPresent()) throw new UserAlreadyExistException();

        User newUser = User.toEntity(signUpReq, gender);
        userRepository.save(newUser);

        String token = jwtTokenProvider.createToken(newUser.getId());

        return SignUpRes.from(token);
    }

    @Override
    @Transactional
    public void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(authenticationUserUtils.getCurrentUserId());

        user.modifyUserInfo(modifyUserInfoReq);
    }


}
