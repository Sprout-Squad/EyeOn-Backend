package Sprout_Squad.EyeOn.domain.user.entity;

import Sprout_Squad.EyeOn.domain.user.enums.Gender;
import Sprout_Squad.EyeOn.global.constant.enums.TF;
import Sprout_Squad.EyeOn.global.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "resident_number", nullable = false)
    private String residentNumber;

    @Column(name = "resident_date", nullable = false)
    private String residentDate;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name ="phone_number", nullable = false)
    private String phoneNumber;

    @Column(name="address", nullable = false)
    private String address;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @Column(name="is_blind", nullable = false)
    private TF isBlind;

}
