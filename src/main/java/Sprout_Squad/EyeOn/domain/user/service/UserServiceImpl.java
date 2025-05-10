package Sprout_Squad.EyeOn.domain.user.service;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.entity.enums.Gender;
import Sprout_Squad.EyeOn.domain.user.exception.UserAlreadyExistException;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import Sprout_Squad.EyeOn.domain.user.web.dto.*;
import Sprout_Squad.EyeOn.global.auth.jwt.UserPrincipal;
import Sprout_Squad.EyeOn.global.auth.jwt.JwtTokenProvider;
import Sprout_Squad.EyeOn.global.converter.OcrResultConverter;
import Sprout_Squad.EyeOn.global.external.service.NaverOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NaverOcrService naverOcrService;

    /**
     * 회원가입
     */
    @Override
    @Transactional
    public SignUpRes signUp(SignUpReq signUpReq) {
        // 카카오 id로 중복 확인
        Optional<User> userByKakaoId = userRepository.findByKakaoId(signUpReq.kakaoId());
        if (userByKakaoId.isPresent()) throw new UserAlreadyExistException();

        // 주민등록번호로 중복 확인
        Optional<User> userByResident = userRepository.findByResidentNumber(signUpReq.residentNumber());
        if(userByResident.isPresent()) throw new UserAlreadyExistException();

        // 주민번호에 따라 성별 인식
        char genderDigit = signUpReq.residentNumber().charAt(7);
        Gender gender = (genderDigit=='1'||genderDigit=='2')?Gender.MALE:Gender.FEMALE;

        // 전화번호로 중복 인식
        Optional<User> userByPhone = userRepository.findByPhoneNumber(signUpReq.phoneNumber());
        if(userByPhone.isPresent()) throw new UserAlreadyExistException();

        User newUser = User.toEntity(signUpReq, gender);
        userRepository.save(newUser);

        String token = jwtTokenProvider.createToken(newUser.getId());

        return SignUpRes.from(token);
    }


    /**
     * 사용자 정보 수정
     */
    @Override
    @Transactional
    public void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq, UserPrincipal userPrincipal) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());
        user.modifyUserInfo(modifyUserInfoReq);
    }

    /**
     * 사용자 정보 조회
     */
    @Override
    public GetUserInfoRes getUserInfo(UserPrincipal userPrincipal) {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(userPrincipal.getId());
        return GetUserInfoRes.from(user);
    }

    /**
     * 주민등록증 인식
     */
    @Override
    public GetResidentInfoRes getResidentInfo(MultipartFile multipartFile) {
        // NaverOCR로 요청
        String ocrJsonString = naverOcrService.requestOcr(multipartFile);

        // 응답 파싱
        GetResidentInfoRes getResidentInfoRes = OcrResultConverter.convertResidentInfo(ocrJsonString);

        return getResidentInfoRes;
    }


}
