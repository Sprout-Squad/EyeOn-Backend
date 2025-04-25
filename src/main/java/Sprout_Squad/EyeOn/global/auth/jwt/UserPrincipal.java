package Sprout_Squad.EyeOn.global.auth.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 *  UserDetails는 "인증된 사용자 정보"를 담는 객체 역할
 */
@Getter
public class UserPrincipal implements UserDetails {
    private final Long id;
    private final Long kakaoId;

    public UserPrincipal(Long id, Long kakaoId) {
        this.id = id;
        this.kakaoId = kakaoId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() { // JWT 기반에서는 사용 X
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(id); // 로그인 식별자로 이용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
