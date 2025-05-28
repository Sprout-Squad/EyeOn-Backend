package Sprout_Squad.EyeOn.domain.user.web.dto;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

public record ModifyUserInfoRes(
        String address,
        String imgUrl
) {
    public static ModifyUserInfoRes from(User user) {
        return new ModifyUserInfoRes(user.getAddress(), user.getProfileImageUrl());
    }
}
