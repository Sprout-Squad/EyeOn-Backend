package Sprout_Squad.EyeOn.domain.user.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifyUserInfoReq(
        @NotBlank(message = "주소는 비어 있을 수 없습니다.")
        String address,

        String profileImageUrl
) {
}
