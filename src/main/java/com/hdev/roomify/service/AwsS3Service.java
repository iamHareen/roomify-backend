package com.hdev.roomify.service;

import com.hdev.roomify.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;     // ✅ Spring @Value (not Lombok)
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class AwsS3Service {

    private final String bucketName;
    private final Region region;
    private final S3Client s3;

    public AwsS3Service(
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.access-key:}") String accessKeyId,          // empty => use default provider
            @Value("${aws.s3.secret-key:}") String secretAccessKey   // empty => use default provider
    ) {
        this.bucketName = bucketName;
        this.region = Region.of(region);

        if (!accessKeyId.isBlank() && !secretAccessKey.isBlank()) {
            // Local/dev: use keys from .env
            this.s3 = S3Client.builder()
                    .region(this.region)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                    .build();
        } else {
            // Prod on AWS: use instance role / env / shared config
            this.s3 = S3Client.builder()
                    .region(this.region)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    /** Uploads a file to S3 and returns the public URL (bucket must allow public read or use CloudFront). */
    public String saveImageToS3(MultipartFile photo) {
        try {
            String original = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "upload";
            String key = "images/" + UUID.randomUUID() + "_" + original;

            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(photo.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // remove if bucket is private
                    .build();

            s3.putObject(req, RequestBody.fromInputStream(photo.getInputStream(), photo.getSize()));

            // Public URL pattern (adjust if using a different region style or CloudFront)
            return "https://" + bucketName + ".s3." + region.id() + ".amazonaws.com/" + key;
        } catch (IOException e) {
            throw new GeneralException("Unable to read upload stream: " + e.getMessage());
        } catch (Exception e) {
            throw new GeneralException("Unable to upload image to S3: " + e.getMessage());
        }
    }
}
