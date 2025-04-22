package Sprout_Squad.EyeOn.domain.document.entity;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "document_form")
public class DocumentForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_form_id")
    private Long id;

    @Column(name = "document_name", nullable = false)
    private String name;

    @Column(name = "document_image_url", nullable = false)
    private String imageUrl;

    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_size", nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
