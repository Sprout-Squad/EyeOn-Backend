package Sprout_Squad.EyeOn.domain.form.entity.enums;

public enum FormType {
    RESUME,            // 이력서
    CERTIFICATE,       // 재직증명서
    CONSENT,           // 위임장
    SELF_INTRO,        // 자기소개서
    REPORT;            // 일일업무보고서, 일일업무일지

    /**
     * 플라스크에서 내려주는 문자열 → FormType 매핑
     */
    public static FormType from(String type) {
        if (type == null) {
            throw new IllegalArgumentException("문서 타입 문자열이 null입니다.");
        }

        switch (type.toLowerCase()) {
            case "resume":
                return RESUME;
            case "certificate":
                return CERTIFICATE;
            case "consent":
                return CONSENT;
            case "self_intro":
                return SELF_INTRO;
            case "report":
                return REPORT;
            default:
                throw new IllegalArgumentException("정의되지 않은 문서 타입: " + type);
        }
    }
}
