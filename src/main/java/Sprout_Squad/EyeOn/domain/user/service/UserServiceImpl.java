package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.exception.UserAlreadyExistException;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpRes;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public SignUpRes signUp(SignUpReq signUpReq) {
        User userByKakaoId = userRepository.findByKakaoId(signUpReq.kakaoId());
        if (userByKakaoId != null) throw new UserAlreadyExistException();

        User userByResident = userRepository.findByResidentNumber(signUpReq.residentNumber());
        if(userByResident != null) throw new UserAlreadyExistException();

        User userByPhone = userRepository.findByPhoneNumber(signUpReq.phoneNumber());
        if(userByPhone != null) throw new UserAlreadyExistException();

        User newUser = User.toEntity(signUpReq);
        userRepository.save(newUser);

        String token = jwtTokenProvider.createToken(signUpReq.kakaoId());

        HttpHeaders httpheaders = new HttpHeaders();
        httpheaders.set("Authorization", "Bearer " + token);

        return SignUpRes.from(token);
    }

}
