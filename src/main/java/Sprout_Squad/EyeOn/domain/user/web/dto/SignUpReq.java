package Sprout_Squad.EyeOn.domain.user.web.dto;

import Sprout_Squad.EyeOn.global.constant.enums.TF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpReq(
        @NotNull(message = "카카오 아이디는 비어 있을 수 없습니다.")
        Long kakaoId,

        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        String name,

        @NotBlank(message = "주민등록번호는 비어 있을 수 없습니다.")
        @Pattern(regexp = "^\\d{6}-[1-4]\\d{6}$", message = "주민등록번호 형식이 유효하지 않습니다.")
        String residentNumber,

        @NotBlank(message = "주민등록번호 발급일자는 비어 있을 수 없습니다.")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\\\d|3[01])$", message = "주민등록번호 발급일자 형식이 유효하지 않습니다.")
        String residentDate,

        @NotBlank(message = "전화번호는 비어 있을 수 없습니다.")
        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 유효하지 않습니다.")
        String phoneNumber,

        @NotBlank(message = "주소는 비어 있을 수 없습니다.")
        String address,

        @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
        @Email(message = "이메일 형식이 유효하지 않습니다.")
        String email,

        String profileImageUrl,

        @NotNull(message = "시각 장애 여부는 비어 있을 수 없습니다.")
        TF isBlind

) {
}
