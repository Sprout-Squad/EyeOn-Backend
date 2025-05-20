package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.domain.document.web.dto.WriteDocsReq;
import Sprout_Squad.EyeOn.global.external.exception.FileNotCreatedException;
import Sprout_Squad.EyeOn.global.external.exception.FontNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final S3Service s3Service;

    /**
     * S3 url을 받아 양식 위에 문서 작성 로직
     */
    public String fillImageFromS3(String s3ImageUrl, List<WriteDocsReq> fields) {
        try (
                InputStream imageStream = s3Service.downloadFile(s3ImageUrl);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            // 1. 이미지 불러오기
            BufferedImage image = ImageIO.read(imageStream);
            if (image == null) {
                throw new IOException("이미지 로딩 실패");
            }

            // 2. 폰트 설정
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/NanumGothic.ttf");
            if (fontStream == null) throw new FontNotFoundException();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(12f);

            Graphics2D g = image.createGraphics();
            g.setFont(font);
            g.setColor(Color.BLACK);

            // 3. 텍스트 삽입
            for (WriteDocsReq field : fields) {
                if (field.value() == null) continue;
                List<Double> bbox = field.bbox();
                float x = bbox.get(0).floatValue();
                float y = bbox.get(1).floatValue();
                g.drawString(field.value(), x, y);
            }

            g.dispose();

            // 4. BufferedImage → ByteArrayOutputStream (JPG 저장)
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 5. S3 업로드
            String fileName = generateImageFileName();
            String s3Key = "filled-docs/" + fileName;
            return s3Service.uploadImageBytes(s3Key, imageBytes);

        } catch (Exception e) {
            throw new FileNotCreatedException();
        }
    }

    /**
     * 텍스트를 pdf 파일로 변환하고 업로드하는 로직 (문서 요약에 사용)
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
     *  pdf를 이미지로 변환
     */
    public String convertPdfToImage(byte[] pdfBytes) throws IOException {
        try (
                PDDocument document = PDDocument.load(pdfBytes);
                ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ) {
            System.out.println("잘 들어와요22");
            PDFRenderer renderer = new PDFRenderer(document);

            System.out.println("잘 들어와요333");
            // 첫 페이지를 이미지로 변환
            BufferedImage bim = renderer.renderImageWithDPI(0, 300);

            System.out.println("잘 들어와요4444");
            ImageIO.write(bim, "jpg", imageOutputStream);
            byte[] imageBytes = imageOutputStream.toByteArray();
            System.out.println("잘 들어와요5");

            String fileName = generateImageFileName();
            String s3Key = "pdf-preview/" + fileName;
            return s3Service.uploadImageBytes(s3Key, imageBytes);
        } catch (IOException e) {
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
     * 이미지 파일명 생성
     */
    private String generateImageFileName() {
        String today = LocalDate.now().toString();
        String uuid = UUID.randomUUID().toString();
        return today + "/" + uuid + ".jpg";
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

    /**
     * img를 pdf로 변환
     */
    public byte[] convertImageToPdf(InputStream imageStream) throws IOException {
        System.out.println("convertImageToPdf(): 호출됨");

        BufferedImage image = ImageIO.read(imageStream);
        if (image == null) {
            System.out.println("이미지 로딩 실패! image == null");
            throw new IOException("ImageIO.read() returned null. 지원되지 않는 형식이거나 잘못된 이미지입니다.");
        }
        return convertImageToPdf(image);
    }

    public byte[] convertImageToPdf(BufferedImage image) throws IOException {
        try (
                PDDocument pdfDoc = new PDDocument();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            pdfDoc.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(pdfDoc, image);
            PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, page);
            contentStream.drawImage(pdImage, 0, 0);
            contentStream.close();

            pdfDoc.save(outputStream);
            return outputStream.toByteArray();
        }
    }


}
