package Sprout_Squad.EyeOn.domain.form.entity;

import Sprout_Squad.EyeOn.domain.form.entity.enums.FormType;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "form")
public class Form extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Long id;

    @Column(name = "form_name", nullable = false)
    private String name;

    @Column(name = "form_image_url", nullable = false)
    private String imageUrl;

    @Column(name = "form_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormType formType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Form toEntity(MultipartFile file, String fileUrl, FormType formType, User user) {
        return Form.builder()
                .name(file.getOriginalFilename())
                .imageUrl(fileUrl)
                .formType(formType)
                .user(user)
                .build();
    }
}
