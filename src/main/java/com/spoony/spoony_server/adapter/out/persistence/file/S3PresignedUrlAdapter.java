package com.spoony.spoony_server.adapter.out.persistence.file;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import java.net.URL;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.spoony.spoony_server.application.port.out.file.S3PresignedUrlPort;
import com.amazonaws.services.s3.AmazonS3;
import com.spoony.spoony_server.global.annotation.Adapter;

import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class S3PresignedUrlAdapter implements S3PresignedUrlPort {


	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Override
	public URL generatePresignedPutUrl(String key, String contentType) {
		Date expiration = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
			.withMethod(HttpMethod.PUT)
			.withContentType(contentType)
			.withExpiration(expiration);

		return amazonS3.generatePresignedUrl(request);
	}

}
