package com.spoony.spoony_server.adapter.out.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.spoony.spoony_server.application.port.out.file.S3DeletePort;

import com.spoony.spoony_server.global.annotation.Adapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

@Adapter
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3Adapter implements S3DeletePort {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void deleteImagesFromS3(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            String key = extractKeyFromUrl(imageUrl);
            log.info("Deleting image from S3 with key: {}", key);
            amazonS3Client.deleteObject(bucket, key);
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        int index = imageUrl.indexOf(".com/");
        if (index == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format: " + imageUrl);
        }
        return imageUrl.substring(index + 5);
    }
}
