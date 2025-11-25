package com.hdev.roomify.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hdev.roomify.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    public String saveImageToS3(MultipartFile photo) {
        try {
            String s3Filename = photo.getOriginalFilename();

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();

            InputStream inputStream = photo.getInputStream();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Filename, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            return "https://" + bucketName + ".s3.amazonaws.com/" + s3Filename;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("Unable to upload image to s3 bucket: " + e.getMessage());
        }
    }
}