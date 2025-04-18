package Sprout_Squad.EyeOn.domain.user.repository;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.domain.user.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByResidentNumber(String residentNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByKakaoId(Long kakaoId);

    default User getUserById(Long id){
        return findById(id).orElseThrow(UserNotFoundException::new);
    }

}
