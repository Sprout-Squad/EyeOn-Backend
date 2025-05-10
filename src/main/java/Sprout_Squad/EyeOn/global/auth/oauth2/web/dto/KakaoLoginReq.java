package Sprout_Squad.EyeOn.global.auth.oauth2.web.dto;

import jakarta.validation.constraints.NotNull;

public record KakaoLoginReq(
        @NotNull(message = "인가 코드는 비어 있을 수 없습니다.")
        String code
) {}
