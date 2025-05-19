package Sprout_Squad.EyeOn.global.flask.enums;

public enum LabelGroup {
    NAME("이름"),
    PHONE("휴대폰"),
    RRN("주민등록번호"),
    EMAIL("이메일"),
    ADDR("주소"),
    DATE("취득일"),
    SCORE("점수"),
    ORG("기관명"),
    PERIOD("기간"),
    PLACE("근무지명"),
    POSITION("직위"),
    DUTY("담당업무"),
    MAJOR("전공"),
    GPA("학점"),
    STATUS("병역여부"),
    TYPE("군별"),
    RANK("계급"),
    DEPT("부서"),
    RELATION("관계"),
    REASON("위임사유"),
    TITLE("업무명"),
    NOTE("특이사항"),
    DETAIL("상세내용"),
    TIME("업무시간"),
    AM_PLAN("오전계획"),
    PM_PLAN("오후계획"),
    SIGN_NAME("지원자"),
    SIGN_SEAL("서명"),
    DATE_DOCUMENT("작성일"),
    PHOTO("사진"),

    // field keywords 전용
    B_DOC_TYPE("문서종류"),
    B_HEADER_PERSONAL("인적사항"),
    B_HEADER_EDU("학력사항"),
    B_HEADER_CERTIFICATE("자격사항"),
    B_HEADER_WORK("경력사항"),
    B_HEADER_EMPLOY("재직사항"),
    B_HEADER_GRANTOR("위임자"),
    B_HEADER_DELEGATE("대리인"),
    B_PURPOSE("용도"),
    B_PLACE_ADDR("근무지주소"),
    B_PLACE_NAME("회사명"),
    B_CEO_SIGN("대표이사 서명"),
    B_SELF_GROWTH("성장과정"),
    B_ACADEMIC_LIFE("학창시절"),
    B_PERSONALITY("성격소개"),
    B_MOTIVATION_FUTURE("지원동기 및 포부"),
    B_HEADER_TODAY_WORK("금일업무요약"),
    B_HEADER_TOMORROW_WORK("익일업무계획"),
    B_REPORT_ISSUE("건의 및 보고사항"),
    B_DATE("날짜"),

    UNKNOWN("알 수 없음");

    private final String displayName;

    LabelGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static LabelGroup fromLabel(String label) {
        if (label == null || !label.startsWith("B-")) return UNKNOWN;
        String key = label.substring(2); // "PERSONAL-NAME", "HEADER-EDU", etc.

        // 직접 매칭
        for (LabelGroup group : values()) {
            if (group.name().equalsIgnoreCase(key.replace("-", "_"))) {
                return group;
            }
        }

        // 말단 요소로 fallback
        String fallbackKey = label.substring(label.lastIndexOf("-") + 1); // e.g., "NAME"
        for (LabelGroup group : values()) {
            if (group.name().equalsIgnoreCase(fallbackKey)) {
                return group;
            }
        }

        return UNKNOWN;
    }
}
