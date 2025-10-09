package com.spoony.spoony_server.adapter.out.persistence.file;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.spoony.spoony_server.application.port.out.file.S3PresignedUrlPort;
import com.amazonaws.services.s3.AmazonS3;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.S3ErrorMessage;

import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class S3PresignedUrlAdapter implements S3PresignedUrlPort {


	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.presigned-url.expiration-minutes:5}")
	private int expirationMinutes;

	@Override
	public URL generatePresignedPutUrl(String key, String contentType) {


		if (!StringUtils.hasText(key)) {
			throw new BusinessException(S3ErrorMessage.INVALID_S3_KEY);
		}
		if (!StringUtils.hasText(contentType)) {
			throw new BusinessException(S3ErrorMessage.INVALID_CONTENT_TYPE);
		}

		try {
			Date expiration = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));

			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
				.withMethod(HttpMethod.PUT)
				.withContentType(contentType)
				.withExpiration(expiration);

			return amazonS3.generatePresignedUrl(request);

		} catch (Exception e) {
			throw new BusinessException(S3ErrorMessage.PRESIGNED_URL_GENERATION_FAIL);
		}
	}

}
