package Sprout_Squad.EyeOn.global.external.service;

import Sprout_Squad.EyeOn.domain.document.entity.enums.DocumentType;
import Sprout_Squad.EyeOn.global.external.exception.S3UrlInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * 파일 업로드
     */
    public String uploadFile(String fileName, MultipartFile file) throws IOException {
        String contentType = "image/jpeg";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName) // 버킷 내에서 저장할 파일 이름
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // URL 생성해서 리턴
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * base64로 인코딩된 image를 업로드
     */
    public String uploadImageBytes(String fileName, byte[] bytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * url로부터 파일 다운로드
     */
    public InputStream downloadFile(String s3Url) {
        String s3Key = extractKeyFromUrl(s3Url);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }


    /**
     * json 업로드
     */
    public String uploadJson(String fileName, String jsonContent) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType("application/json")
                .build();

        s3Client.putObject(request, RequestBody.fromString(jsonContent));

        // url 생성해서 리턴
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * base64로 인코딩된 항목을 업로드
     */
    public String uploadPdfBytes(String fileName, byte[] bytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        // URL 생성해서 리턴
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }


    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        String fileName = extractKeyFromUrl(fileUrl);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * 파일 크기 반환
     */
    public long getSize(String fileUrl) {
        String fileName = extractKeyFromUrl(fileUrl);
        HeadObjectRequest headBucketRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headBucketRequest);
        return headObjectResponse.contentLength();
    }

    /**
     * S3 url에서 Key 값 추출
     */
    public String extractKeyFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            if (path == null || path.length() <= 1) {
                throw new S3UrlInvalidException();
            }
            return path.substring(1);
        } catch (URISyntaxException e) {
            throw new S3UrlInvalidException();
        }
    }

    /**
     * 파일명 생성
     */
    public String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        // 파일 확장자
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 파일명 (확장자 제외)에서 특수문자 제거 또는 대체
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."))
                .replaceAll("[^a-zA-Z0-9-_]", "_");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 최종 경로
        return "eyeon/" + timestamp + "/" + baseName + extension;
    }

    public String generatePdfFileName() {
        String uuid = UUID.randomUUID().toString();
        String today = LocalDate.now().toString();
        return "eyeon/" + today + "/" + uuid + ".pdf";
    }



}
