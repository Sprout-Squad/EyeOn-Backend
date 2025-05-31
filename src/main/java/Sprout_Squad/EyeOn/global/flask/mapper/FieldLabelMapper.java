package Sprout_Squad.EyeOn.global.flask.mapper;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class FieldLabelMapper {

    private final Map<String, Map<String, String>> labelMap;

    // 클래스 필드 선언 추가
    private final Map<String, String> resume;
    private final Map<String, String> certificate;
    private final Map<String, String> consent;
    private final Map<String, String> selfIntro;
    private final Map<String, String> report;
    private final Map<String, String> common;

    public FieldLabelMapper() {
        Map<String, Map<String, String>> tempMap = new HashMap<>();

        this.resume = Map.ofEntries(
                Map.entry("B-HEADER-EDU", "학력사항"),
                Map.entry("B-HEADER-CERTIFICATE", "자격 및 면허"),
                Map.entry("B-HEADER-WORK", "경력사항"),
                Map.entry("B-HEADER-PERSONAL", "인적사항"),
                Map.entry("B-HEADER-MILITARY", "병역사항"),
                Map.entry("B-PERSONAL-PHOTO", "[인적사항] 사진"),
                Map.entry("B-PERSONAL-NAME", "[인적사항] 이름"),
                Map.entry("B-PERSONAL-RRN", "[인적사항] 주민등록번호"),
                Map.entry("B-PERSONAL-PHONE", "[인적사항] 연락처"),
                Map.entry("B-PERSONAL-ADDR", "[인적사항] 주소"),
                Map.entry("B-PERSONAL-EMAIL", "[인적사항] 이메일"),
                Map.entry("B-EDU-PERIOD", "[학력사항] 재학 기간"),
                Map.entry("B-EDU-NAME", "[학력사항] 학교명"),
                Map.entry("B-EDU-MAJOR", "[학력사항] 학과"),
                Map.entry("B-EDU-GPA", "[학력사항] 학점"),
                Map.entry("B-WORK-PERIOD", "[경력사항] 재직 기간"),
                Map.entry("B-WORK-PLACE", "[경력사항] 업체명"),
                Map.entry("B-WORK-POSITION", "[경력사항] 직위"),
                Map.entry("B-WORK-DUTY", "[경력사항] 담당업무"),
                Map.entry("B-CERTIFICATE-DATE", "[자격사항] 자격증 취득일"),
                Map.entry("B-CERTIFICATE-NAME", "[자격사항] 자격증 명"),
                Map.entry("B-CERTIFICATE-SCORE", "[자격사항] 점수(급)"),
                Map.entry("B-CERTIFICATE-ORG", "[자격사항] 기관명"),
                Map.entry("B-MILITARY-PERIOD", "[병역사항] 병역 기간"),
                Map.entry("B-MILITARY-STATUS", "[병역사항] 병역여부"),
                Map.entry("B-MILITARY-TYPE", "[병역사항] 군별"),
                Map.entry("B-MILITARY-RANK", "[병역사항] 계급")
        );

        this.certificate = Map.ofEntries(
                Map.entry("B-DOC-TYPE", "재직증명서"),
                Map.entry("B-HEADER-PERSONAL", "인적사항"),
                Map.entry("B-PERSONAL-NAME", "[인적사항] 이름"),
                Map.entry("B-PERSONAL-RRN", "[인적사항] 주민등록번호"),
                Map.entry("B-PERSONAL-ADDR", "[인적사항] 주소"),
                Map.entry("B-HEADER-EMPLOY", "재직사항"),
                Map.entry("B-EMPLOY-DEPT", "[재직사항] 부서"),
                Map.entry("B-EMPLOY-POSITION", "[재직사항] 직위"),
                Map.entry("B-EMPLOY-PERIOD", "[재직사항] 기간"),
                Map.entry("B-PURPOSE", "용도"),
                Map.entry("B-SIGN-YEAR", "[서명일자] 년"),
                Map.entry("B-SIGN-MONTH", "[서명일자] 월"),
                Map.entry("B-SIGN-DAY", "[서명일자] 일"),
                Map.entry("B-PLACE-ADDR", "근무지 주소"),
                Map.entry("B-PLACE-NAME", "회사명"),
                Map.entry("B-CEO-SIGN", "대표이사"),
                Map.entry("SIGN-SEAL", "서명")
        );

        this.consent = Map.ofEntries(
                Map.entry("B-DOC-TYPE", "위임장"),
                Map.entry("B-HEADER-GRANTOR", "위임자(본인)"),
                Map.entry("B-GRANTOR-NAME", "[위임자] 위임자 이름"),
                Map.entry("B-GRANTOR-RRN", "[위임자] 주민등록번호"),
                Map.entry("B-GRANTOR-ADDR", "[위임자] 주소"),
                Map.entry("B-GRANTOR-PHONE", "[위임자] 연락처"),
                Map.entry("B-GRANTOR-REASON", "[위임자] 위임사유"),
                Map.entry("B-HEADER-DELEGATE", "수임자(대리인)"),
                Map.entry("B-DELEGATE-NAME", "[수임자] 수임자 이름"),
                Map.entry("B-DELEGATE-RRN", "[수임자] 주민등록번호"),
                Map.entry("B-DELEGATE-ADDR", "[수임자] 주소"),
                Map.entry("B-DELEGATE-PHONE", "[수임자] 연락처"),
                Map.entry("B-DELEGATE-RELATION", "[수임자] 관계"),
                Map.entry("B-GRANT-CONTENT", "위임 내용"),
                Map.entry("B-SIGN-YEAR", "[서명일자] 연도"),
                Map.entry("B-SIGN-MONTH", "[서명일자] 월"),
                Map.entry("B-SIGN-DAY", "[서명일자] 일"),
                Map.entry("B-SIGN-NAME", "위임자"),
                Map.entry("SIGN-SEAL", "서명")
        );

        this.selfIntro = Map.ofEntries(
                Map.entry("B-DOC-TYPE", "자기소개서"),
                Map.entry("B-SELF-GROWTH", "성장 과정"),
                Map.entry("B-ACADEMIC-LIFE", "학창 시절"),
                Map.entry("B-PERSONALITY", "성격 소개"),
                Map.entry("B-MOTIVATION-FUTURE", "지원동기 및 포부")
        );

        this.report = Map.ofEntries(
                Map.entry("B-DOC-TYPE", "일일 업무 보고서"),
                Map.entry("B-DATE", "날짜"),
                Map.entry("B-NAME", "성명"),
                Map.entry("B-DEPT", "부서"),
                Map.entry("B-POSITION", "직책"),
                Map.entry("B-HEADER-TODAY-WORK", "금일 업무 요약"),
                Map.entry("B-TODAY-WORK-TIME", "[금일 업무] 업무 시간"),
                Map.entry("B-TODAY-WORK-TITLE", "[금일 업무] 업무명"),
                Map.entry("B-TODAY-WORK-DETAIL", "[금일 업무] 상세 내용"),
                Map.entry("B-TODAY-WORK-NOTE", "[금일 업무] 특이사항"),
                Map.entry("B-HEADER-TOMORROW-WORK", "익일 업무 계획"),
                Map.entry("B-TOMORROW-WORK-AM-PLAN", "익일 오전 업무 계획"),
                Map.entry("B-TOMORROW-WORK-PM-PLAN", "익일 오후 업무 계획"),
                Map.entry("B-REPORT-ISSUE", "건의 및 보고사항")
        );

        this.common = Map.ofEntries(
                Map.entry("B-SIGN-YEAR", "[서명일자] 연도"),
                Map.entry("B-SIGN-MONTH", "[서명일자] 월"),
                Map.entry("B-SIGN-DAY", "[서명 일자] 일"),
                Map.entry("B-SIGN-NAME", "지원자"),
                Map.entry("B-DATE-DOCUMENT", "작성일자"),
                Map.entry("B-SIGN-SEAL", "(인)")
        );

        tempMap.put("resume", this.resume);
        tempMap.put("certificate", this.certificate);
        tempMap.put("consent", this.consent);
        tempMap.put("self_intro", this.selfIntro);
        tempMap.put("report", this.report);
        tempMap.put("common", this.common);

        this.labelMap = Collections.unmodifiableMap(tempMap);
    }

    public String getDisplayName(String category, String label) {
        return labelMap.getOrDefault(category, Collections.emptyMap())
                .getOrDefault(label, label);
    }

    public Map<String, String> getCategory(String category) {
        return labelMap.getOrDefault(category, Collections.emptyMap());
    }

    public Map<String, Map<String, String>> getAll() {
        return labelMap;
    }

    public Map<String, Map<String, String>> getContextualLabels() {
        return Map.of(
                "resume", resume,
                "certificate", certificate,
                "consent", consent,
                "self_intro", selfIntro,
                "report", report,
                "common", common
        );
    }
}
