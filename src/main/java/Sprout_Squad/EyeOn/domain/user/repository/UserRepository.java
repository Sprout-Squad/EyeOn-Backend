package Sprout_Squad.EyeOn.domain.user.repository;

import Sprout_Squad.EyeOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByResidentNumber(String residentNumber);
    User findByPhoneNumber(String phoneNumber);
    User findByKakaoId(Long kakaoId);
}
