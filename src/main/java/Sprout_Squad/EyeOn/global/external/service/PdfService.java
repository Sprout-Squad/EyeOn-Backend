package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.global.external.exception.FileNotCreatedException;
import Sprout_Squad.EyeOn.global.external.exception.FontNotFoundException;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PdfService {

    /**
     * 텍스트를 pdf 파일로 변환하는 로직
     */
    public byte[] textToPdf(String content) {
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
            if (fontStream == null) {
                throw new FontNotFoundException();
            }

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

            // PDF 바이트 반환
            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new FileNotCreatedException();
        }
    }
}
