package Sprout_Squad.EyeOn.global.converter;

import Sprout_Squad.EyeOn.domain.user.web.dto.GetResidentInfoRes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OcrResultConverter {
    public static GetResidentInfoRes convertResidentInfo(String ocrResultJson) {
        JSONObject json = new JSONObject(ocrResultJson);
        JSONArray fields = json.getJSONArray("images")
                .getJSONObject(0)
                .getJSONArray("fields");

        String name = null;
        String residentNumber = null;
        String residentDate = null;
        List<String> addressParts = new ArrayList<>();

        // 이름은 두 번째 field로 간주
        if (fields.length() > 1) {
            String rawName = fields.getJSONObject(1).getString("inferText");
            name = cleanName(rawName);
        }

        boolean reachedIssueDate = false;

        for (int i = 0; i < fields.length(); i++) {
            String text = fields.getJSONObject(i).getString("inferText");

            if (!reachedIssueDate && text.matches("\\d{4}\\.\\d{1,2}\\.\\d{1,2}")) {
                residentDate = text;
                reachedIssueDate = true;
                continue;
            }

            if (residentNumber == null && text.matches("\\d{6}-\\d{7}")) {
                residentNumber = text;
            } else if (!reachedIssueDate && i > 1) { // 0=타이틀, 1=이름, 이후가 주소
                addressParts.add(text);
            }
        }

        String address = String.join(" ", addressParts);
        return new GetResidentInfoRes(name, residentNumber, residentDate, address);
    }


    private static String cleanName(String rawName) {
        return rawName.replaceAll("\\(.*\\)", "").trim();
    }
}
