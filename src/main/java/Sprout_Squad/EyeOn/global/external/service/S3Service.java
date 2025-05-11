package Sprout_Squad.EyeOn.global.external.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDate;
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
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName) // 버킷 내에서 저장할 파일 이름
                .contentType(file.getContentType())
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
     * base64로 인코딩된 pdf를 업로드
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
        int index = fileUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
        return fileUrl.substring(index);
    }

    /**
     * 파일명 생성
     */
    public String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String today = LocalDate.now().toString();

        return "form/" + today + "/" + uuid + extension;
    }


}
