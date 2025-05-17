package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.global.external.exception.FileNotCreatedException;
import Sprout_Squad.EyeOn.global.external.exception.FontNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final S3Service s3Service;

    /**
     * 텍스트를 pdf 파일로 변환하고 업로드하는 로직
     */
    public String textToPdf(String content) {
        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            // 페이지 생성
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // 폰트 로드
            InputStream fontStream = PdfService.class.getClassLoader()
                    .getResourceAsStream("fonts/NanumGothic.ttf");
            if (fontStream == null) throw new FontNotFoundException();

            PDType0Font font = PDType0Font.load(document, fontStream);

            // 텍스트 작성
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.setLeading(14.5f); // 줄 간격
            contentStream.newLineAtOffset(50, 750); // 좌측 여백, 시작 위치

            // 줄바꿈 처리
            for (String line : content.split("\n")) {
                contentStream.showText(line.trim());
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();


            // PDF → 바이트 배열
            document.save(outputStream);
            byte[] pdfBytes = outputStream.toByteArray();

            // 파일명 생성 및 S3 업로드
            String fileName = generatePdfFileName();
            String s3Key = "pdf/" + fileName;
            return s3Service.uploadPdfBytes(s3Key, pdfBytes);

        } catch (Exception e) {
            throw new FileNotCreatedException();
        }
    }

    /**
     * pdf 파일명 생성
     */
    private String generatePdfFileName() {
        String today = LocalDate.now().toString();
        String uuid = UUID.randomUUID().toString();
        return today + "/" + uuid + ".pdf";
    }

    /**
     * 확장자 명 추출
     */
    public String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1);
        } else {
            return "jpg";
        }
    }
}
