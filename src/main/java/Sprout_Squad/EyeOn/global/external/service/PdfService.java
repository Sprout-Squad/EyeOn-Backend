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
     * S3 url을 받아 기존 양식 위에 적힌 부분들을 하얀색으로 덮어 씌우고 재작성하는 로직
     */
    public String rewriteImg(String s3ImageUrl, List<WriteDocsReq> fields) {
        System.out.println("호출됨 :" + fields);
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
                System.out.println("[필드 처리 시작] " + field);

                if (field.value() == null) continue;
                // bbox : [x0, y0, x1, y1]
                List<Double> bbox = field.bbox();

                float imgWidth = image.getWidth();
                float imgHeight = image.getHeight();

                // bbox 좌표를 이미지 크기에 맞게 변환
                List<? extends Number> bboxRaw = field.bbox();
                float x0 = bboxRaw.get(0).floatValue() * imgWidth / 1000;
                float y0 = bboxRaw.get(1).floatValue() * imgHeight / 1000;
                float x1 = bboxRaw.get(2).floatValue() * imgWidth / 1000;
                float y1 = bboxRaw.get(3).floatValue() * imgHeight / 1000;

                // 텍스트를 박스 안에 적절히 배치 (좌표 보정)
                float boxHeight = y1 - y0;
                x0 += 2f;
                y0 += boxHeight * 0.75f; // 텍스트를 박스의 적절한 위치로 이동 (상단보다는 조금 아래쪽)

                System.out.printf("[좌표] x0=%.2f, y0=%.2f, x1=%.2f, y1=%.2f%n", x0, y0, x1, y1);


                // 박스를 흰색으로 지우기
                g.setColor(Color.WHITE);
                g.fillRect((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0));

                g.setColor(Color.BLACK);
                //g.drawRect((int) x0, (int) y0, (int) (x1 - x0), (int) boxHeight); // 디버깅용 사각형 그리기
                // 폰트 크기를 bbox 높이에 맞춰 동적으로 설정
                Font dynamicFont = font.deriveFont( 20f);  // 박스 높이에 맞춰 폰트 크기 조정
                g.setFont(dynamicFont);

                System.out.println("[텍스트 출력] value=\"" + field.value() + "\" at (" + x0 + ", " + y0 + ")");

                // 텍스트 삽입
                g.drawString(field.value(), x0, y0);
            }


            g.dispose();

            System.out.println("4번");
            // 4. BufferedImage → ByteArrayOutputStream (JPG 저장)
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            System.out.println("5번");
            // 5. S3 업로드
            String fileName = generateImageFileName();
            String s3Key = "filled-docs/" + fileName;
            return s3Service.uploadImageBytes(s3Key, imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            throw new FileNotCreatedException();
        }
    }

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
                // bbox : [x0, y0, x1, y1]
                List<Double> bbox = field.bbox();

                float imgWidth = image.getWidth();
                float imgHeight = image.getHeight();

                // bbox 좌표를 이미지 크기에 맞게 변환
                float x0 = bbox.get(0).floatValue() * imgWidth / 1000;  // x0
                float y0 = bbox.get(1).floatValue() * imgHeight / 1000; // y0
                float x1 = bbox.get(2).floatValue() * imgWidth / 1000;  // x1
                float y1 = bbox.get(3).floatValue() * imgHeight / 1000; // y1

                // 텍스트를 박스 안에 적절히 배치 (좌표 보정)
                float boxHeight = y1 - y0;
                x0 += 2f;
                y0 += boxHeight * 0.75f; // 텍스트를 박스의 적절한 위치로 이동 (상단보다는 조금 아래쪽)


                g.setColor(Color.BLACK);
                //g.drawRect((int) x0, (int) y0, (int) (x1 - x0), (int) boxHeight); // 디버깅용 사각형 그리기

                // 폰트 크기를 bbox 높이에 맞춰 동적으로 설정 (선택 사항)
                Font dynamicFont = font.deriveFont( 20f);  // 박스 높이에 맞춰 폰트 크기 조정
                g.setFont(dynamicFont);

                // 텍스트 삽입
                g.drawString(field.value(), x0, y0);
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
            PDFRenderer renderer = new PDFRenderer(document);

            // 첫 페이지를 이미지로 변환
            BufferedImage bim = renderer.renderImageWithDPI(0, 300);

            ImageIO.write(bim, "jpg", imageOutputStream);
            byte[] imageBytes = imageOutputStream.toByteArray();

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
