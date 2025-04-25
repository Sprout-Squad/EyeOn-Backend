package Sprout_Squad.EyeOn.global.external;

import Sprout_Squad.EyeOn.global.converter.ImgConverter;
import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class NaverOcrService {
    @Value("${application.clova.ocr.secret-key}")
    private String secretKey;

    @Value("${application.clova.ocr.api-url}")
    private String apiUrl;

    public String requestOcr(MultipartFile multipartFile){
        try{
            // Base64로 변환
            String base64Image = ImgConverter.toBase64(multipartFile);

            // JSON 메타데이터 구성
            JSONObject image = new JSONObject();
            image.put("format", "jpg");
            image.put("name", multipartFile.getOriginalFilename());
            image.put("data", base64Image);

            JSONArray images = new JSONArray();
            images.put(image);

            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());
            json.put("images", images);

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(3000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-OCR-SECRET", secretKey);
            con.connect();

            // 전송
            try (OutputStream out = con.getOutputStream()) {
                out.write(json.toString().getBytes(StandardCharsets.UTF_8));
                out.flush();
            }

            // 응답 처리
            BufferedReader br;
            if (con.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
