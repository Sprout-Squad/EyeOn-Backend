package Sprout_Squad.EyeOn.global.flask.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FieldTargetMapper {
    private static final Map<String, String> fieldToTargetField;

    static {
        Map<String, String> map = new HashMap<>();

        // 이력서 - 자격
        map.put("B-CERTIFICATE-DATE", "B-CERTIFICATE-DATE-FIELD");
        map.put("B-CERTIFICATE-NAME", "B-CERTIFICATE-NAME-FIELD");
        map.put("B-CERTIFICATE-ORG", "B-CERTIFICATE-ORG-FIELD");
        map.put("B-CERTIFICATE-SCORE", "B-CERTIFICATE-SCORE-FIELD");

        // 이력서 - 학업
        map.put("B-EDU-GPA", "B-EDU-GPA-FIELD");
        map.put("B-EDU-MAJOR", "B-EDU-MAJOR-FIELD");
        map.put("B-EDU-NAME", "B-EDU-NAME-FIELD");
        map.put("B-EDU-PERIOD", "B-EDU-PERIOD-FIELD");

        // 이력서 - 군복무
        map.put("B-MILITARY-PERIOD", "B-MILITARY-PERIOD-FIELD");
        map.put("B-MILITARY-RANK", "B-MILITARY-RANK-FIELD");
        map.put("B-MILITARY-STATUS", "B-MILITARY-STATUS-FIELD");
        map.put("B-MILITARY-TYPE", "B-MILITARY-TYPE-FIELD");

        // 이력서 - 경력
        map.put("B-WORK-DUTY", "B-WORK-DUTY-FIELD");
        map.put("B-WORK-PERIOD", "B-WORK-PERIOD-FIELD");
        map.put("B-WORK-PLACE", "B-WORK-PLACE-FIELD");
        map.put("B-WORK-NAME", "B-WORK-NAME-FIELD");
        map.put("B-WORK-POSITION", "B-WORK-POSITION-FIELD");

        // 업무일지
        map.put("B-DATE", "B-DATE-FIELD");
        map.put("B-DEPT", "B-DEPT-FIELD");
        map.put("B-NAME", "B-NAME-FIELD");
        map.put("B-POSITION", "B-POSITION-FIELD");
        map.put("B-REPORT-ISSUE", "B-REPORT-ISSUE-FIELD");
        map.put("B-TODAY-WORK-DETAIL", "B-TODAY-WORK-DETAIL-FIELD");
        map.put("B-TODAY-WORK-NOTE", "B-TODAY-WORK-NOTE-FIELD");
        map.put("B-TODAY-WORK-TIME", "B-TODAY-WORK-TIME-FIELD");
        map.put("B-TODAY-WORK-TITLE", "B-TODAY-WORK-TITLE-FIELD");
        map.put("B-TOMORROW-WORK-AM-PLAN", "B-TOMORROW-WORK-AM-PLAN-FIELD");
        map.put("B-TOMORROW-WORK-PM-PLAN", "B-TOMORROW-WORK-PM-PLAN-FIELD");


        // 자기소개서
        map.put("B-ACADEMIC-LIFE", "B-ACADEMIC-LIFE-FIELD");
        map.put("B-MOTIVATION-FUTURE", "B-MOTIVATION-FUTURE-FIELD");
        map.put("B-PERSONALITY", "B-PERSONALITY-FIELD");
        map.put("B-SELF-GROWTH", "B-SELF-GROWTH-FIELD");

        // 재직증명서
        map.put("B-CEO-SIGN", "B-CEO-SIGN-FIELD");
        map.put("B-EMPLOY-DEPT", "B-EMPLOY-DEPT-FIELD");
        map.put("B-EMPLOY-PERIOD", "B-EMPLOY-PERIOD-FIELD");
        map.put("B-EMPLOY-POSITION", "B-EMPLOY-POSITION-FIELD");
        map.put("B-PLACE-ADDR", "B-PLACE-ADDR-FIELD");
        map.put("B-PLACE-NAME", "B-PLACE-NAME-FIELD");
        map.put("B-PURPOSE", "B-PURPOSE-FIELD");

        // 위임장
        map.put("B-DELEGATE-ADDR", "B-DELEGATE-ADDR-FIELD");
        map.put("B-DELEGATE-NAME", "B-DELEGATE-NAME-FIELD");
        map.put("B-DELEGATE-PHONE", "B-DELEGATE-PHONE-FIELD");
        map.put("B-DELEGATE-RELATION", "B-DELEGATE-RELATION-FIELD");
        map.put("B-DELEGATE-RRN", "B-DELEGATE-RRN-FIELD");
        map.put("B-GRANT-CONTENT", "B-GRANT-CONTENT-FIELD");
        map.put("B-GRANTOR-ADDR", "B-GRANTOR-ADDR-FIELD");
        map.put("B-GRANTOR-NAME", "B-GRANTOR-NAME-FIELD");
        map.put("B-GRANTOR-PHONE", "B-GRANTOR-PHONE-FIELD");
        map.put("B-GRANTOR-REASON", "B-GRANTOR-REASON-FIELD");
        map.put("B-GRANTOR-RRN", "B-GRANTOR-RRN-FIELD");

        // 오타로 보이는 항목
        //map.put("B-GRANT-CONTENT", "B-GRANT-CONTENT-FILED");

        // ?
        map.put("B-DATE-DOCUMENT", "B-DATE-DOCUMENT-FIELD");

        // common
        map.put("B-SIGN-DAY", "B-SIGN-DAY-FIELD");
        map.put("B-SIGN-MONTH", "B-SIGN-MONTH-FIELD");
        map.put("B-SIGN-NAME", "B-SIGN-NAME-FIELD");
        map.put("B-SIGN-YEAR", "B-SIGN-YEAR-FIELD");
        map.put("B-PERSONAL-ADDR", "B-PERSONAL-ADDR-FIELD");
        map.put("B-PERSONAL-EMAIL", "B-PERSONAL-EMAIL-FIELD");
        map.put("B-PERSONAL-NAME", "B-PERSONAL-NAME-FIELD");
        map.put("B-PERSONAL-PHONE", "B-PERSONAL-PHONE-FIELD");
        map.put("B-PERSONAL-RRN", "B-PERSONAL-RRN-FIELD");
        map.put("B-PERSONAL-PHOTO", "B-PERSONAL-PHOTO-FIELD");

        fieldToTargetField = Collections.unmodifiableMap(map);
    }

    public static String getTargetField(String field) {
        return fieldToTargetField.get(field);
    }

    public static boolean hasTargetField(String field) {
        return fieldToTargetField.containsKey(field);
    }

    public static Map<String, String> getAllMappings() {
        return fieldToTargetField;
    }

    public static String getFieldByTargetField(String targetField) {
        for (Map.Entry<String, String> entry : fieldToTargetField.entrySet()) {
            if (entry.getValue().equals(targetField)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
