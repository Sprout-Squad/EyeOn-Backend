package Sprout_Squad.EyeOn.global.external;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrTestController {

    private final NaverOcrService naverOcrService;

    @PostMapping
    public String testOcr(@RequestParam("file") MultipartFile file) {
        return naverOcrService.requestOcr(file); // 응답 JSON 그대로 리턴
    }
}
