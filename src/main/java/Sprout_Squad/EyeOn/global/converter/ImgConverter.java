package Sprout_Squad.EyeOn.global.converter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class ImgConverter {
    /**
     * multiPartFile을 Base64로 인코딩
     */
    public static String toBase64(MultipartFile file) {
        try{
            byte[] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 인코딩 실패",e);
        }
    }
}
