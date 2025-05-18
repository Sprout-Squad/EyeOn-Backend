package Sprout_Squad.EyeOn.global.flask.mapper;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class FieldLabelMapper {
    private final Map<String, Map<String, String>> groupToKorMap = new HashMap<>();

    public FieldLabelMapper() {
        groupToKorMap.put("resume", Map.ofEntries(
                Map.entry("PHOTO", "사진"),
                Map.entry("NAME", "이름"),
                Map.entry("RRN", "주민등록번호"),
                Map.entry("PHONE", "휴대폰"),
                Map.entry("EMAIL", "이메일"),
                Map.entry("ADDR", "주소"),
                Map.entry("ORG", "기관명"),
                Map.entry("DATE", "취득일"),
                Map.entry("SCORE", "점수(급)"),
                Map.entry("PLACE", "업체명"),
                Map.entry("PERIOD", "기간"),
                Map.entry("POSITION", "직위"),
                Map.entry("DUTY", "담당업무"),
                Map.entry("MAJOR", "전공"),
                Map.entry("GPA", "학점"),
                Map.entry("STATUS", "병역여부"),
                Map.entry("TYPE", "군별"),
                Map.entry("RANK", "계급")
        ));

        groupToKorMap.put("certificate", Map.ofEntries(
                Map.entry("NAME", "이름"),
                Map.entry("RRN", "주민등록번호"),
                Map.entry("ADDR", "주소"),
                Map.entry("DEPT", "부서"),
                Map.entry("PERIOD", "기간"),
                Map.entry("POSITION", "직위")
        ));

        groupToKorMap.put("consent", Map.ofEntries(
                Map.entry("NAME", "이름"),
                Map.entry("RRN", "주민등록번호"),
                Map.entry("PHONE", "휴대폰"),
                Map.entry("ADDR", "주소"),
                Map.entry("RELATION", "관계"),
                Map.entry("REASON", "위임사유")
        ));

        groupToKorMap.put("self_intro", Map.of());

        groupToKorMap.put("report", Map.ofEntries(
                Map.entry("DATE", "날짜"),
                Map.entry("NAME", "성명"),
                Map.entry("POSITION", "직위"),
                Map.entry("DEPT", "부서"),
                Map.entry("TIME", "업무시간"),
                Map.entry("DETAIL", "상세내용"),
                Map.entry("TITLE", "업무명"),
                Map.entry("NOTE", "특이사항"),
                Map.entry("AM-PLAN", "오전"),
                Map.entry("PM-PLAN", "오후")
        ));

        groupToKorMap.put("common", Map.ofEntries(
                Map.entry("B-SIGN-YEAR", "년"),
                Map.entry("B-SIGN-MONTH", "월"),
                Map.entry("B-SIGN-DAY", "일"),
                Map.entry("B-SIGN-NAME", "지원자"),
                Map.entry("B-DATE-DOCUMENT", "작성일자"),
                Map.entry("SIGN-SEAL", "(인)")
        ));
    }

    public String getKorField(String docType, String groupKey) {
        return groupToKorMap
                .getOrDefault(docType, Collections.emptyMap())
                .getOrDefault(groupKey, groupKey);
    }
}
