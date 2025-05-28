package Sprout_Squad.EyeOn.domain.user.entity;

import Sprout_Squad.EyeOn.domain.user.entity.enums.Gender;
import Sprout_Squad.EyeOn.domain.user.web.dto.ModifyUserInfoReq;
import Sprout_Squad.EyeOn.domain.user.web.dto.SignUpReq;
import Sprout_Squad.EyeOn.global.constant.enums.TF;
import Sprout_Squad.EyeOn.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "resident_number", nullable = false, unique = true)
    private String residentNumber;

    @Column(name = "resident_date", nullable = false)
    private String residentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name ="phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name="address", nullable = false)
    private String address;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="is_blind", nullable = false)
    private TF isBlind;

    public void modifyUserInfo(ModifyUserInfoReq modifyUserInfoReq) {
        this.address = modifyUserInfoReq.address();
        this.profileImageUrl = modifyUserInfoReq.profileImageUrl();
    }

    public static User toEntity(SignUpReq signUpReq, Gender gender) {
        String rawPhone = signUpReq.phoneNumber();
        String formattedPhone = formatPhoneNumber(rawPhone);

        return User.builder()
                .kakaoId(signUpReq.kakaoId())
                .name(signUpReq.name())
                .residentNumber(signUpReq.residentNumber())
                .residentDate(signUpReq.residentDate())
                .gender(gender)
                .phoneNumber(formattedPhone)
                .address(signUpReq.address())
                .email(signUpReq.email())
                .profileImageUrl(signUpReq.profileImageUrl())
                .isBlind(signUpReq.isBlind())
                .build();
    }

    // 전화번호 포맷
    private static String formatPhoneNumber(String number) {
        if (number == null) return null;

        number = number.replaceAll("[^0-9]", ""); // 숫자만 추출

        if (number.length() == 10) {
            return number.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else if (number.length() == 11) {
            return number.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else {
            return number; // 길이 이상하면 그대로 반환
        }
    }

}
