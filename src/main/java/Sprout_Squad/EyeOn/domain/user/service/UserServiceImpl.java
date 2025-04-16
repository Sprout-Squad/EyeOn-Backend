package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.entity.enums.Gender;
import Sprout_Squad.EyeOn.domain.user.exception.UserAlreadyExistException;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
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

        String token = jwtTokenProvider.createToken(signUpReq.kakaoId());

        HttpHeaders httpheaders = new HttpHeaders();
        httpheaders.set("Authorization", "Bearer " + token);

        return SignUpRes.from(token);
    }

}
