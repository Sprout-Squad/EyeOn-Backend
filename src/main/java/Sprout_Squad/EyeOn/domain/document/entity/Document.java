package Sprout_Squad.EyeOn.domain.document.entity;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.domain.form.entity.Form;
import Sprout_Squad.EyeOn.domain.user.entity.User;
import Sprout_Squad.EyeOn.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "document")
public class Document extends BaseEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private Form form;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    /**
     * 문서 이미지
     */
    @Column(name = "document_image_url", nullable = false)
    private String documentImageUrl;

    /**
     * 문서 pdf 파일
     */
    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    public void modifyDocument(String documentImageUrl, String documentUrl) {
        this.documentImageUrl = documentImageUrl;
        this.documentUrl = documentUrl;
    }

    public static Document toEntity(DocumentType documentType, String imageUrl, String documentUrl, Form form, String fileName, User user){
        return Document.builder()
                .user(user)
                .form(form)
                .documentName(fileName) // 양식 이름과 동일하게 사용
                .documentImageUrl(imageUrl) // 작성된 문서 이미지 형태
                .documentUrl(documentUrl) // pdf 형태
                .documentType(documentType)
                .build();
    }


}
