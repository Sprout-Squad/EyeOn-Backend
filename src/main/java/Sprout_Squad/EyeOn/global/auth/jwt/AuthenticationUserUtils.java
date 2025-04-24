package Sprout_Squad.EyeOn.global.auth.jwt;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUserUtils {
    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("auth = " + authentication);
//        System.out.println("principal = " + authentication.getPrincipal());
        return Long.parseLong(authentication.getName());
    }

    public User getCurrentUser() {
        // 사용자가 존재하지 않을 경우 -> UserNotFoundException
        User user = userRepository.getUserById(getCurrentUserId());
        return user;
    }
}
