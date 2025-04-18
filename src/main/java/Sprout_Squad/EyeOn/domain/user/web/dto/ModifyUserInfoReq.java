package Sprout_Squad.EyeOn.domain.user.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifyUserInfoReq(
        @NotBlank(message = "주소를 입력해주세요.")
        String address,

        String profileImageUrl
) {
}
