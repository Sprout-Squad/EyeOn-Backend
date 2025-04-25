package Sprout_Squad.EyeOn.global.auth.jwt;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * UserDetailsService는 Spring Security가 "사용자 정보를 조회"할 때 사용하는 서비스 역할
 */
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * username은 사용자를 구분하는 식별자
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.getUserById(Long.parseLong(userId));

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()), "", Collections.emptyList()
        );
    }
}
