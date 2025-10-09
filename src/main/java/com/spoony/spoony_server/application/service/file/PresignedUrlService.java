package com.spoony.spoony_server.application.service.file;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


import org.springframework.stereotype.Service;

import com.spoony.spoony_server.adapter.dto.file.response.PresignedUrlResponseDTO;
import com.spoony.spoony_server.application.port.command.file.PresignedUrlCreateCommand;
import com.spoony.spoony_server.application.port.in.file.PresignedUrlCreateUseCase;
import com.spoony.spoony_server.application.port.out.file.S3PresignedUrlPort;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresignedUrlService implements PresignedUrlCreateUseCase {

	private final S3PresignedUrlPort s3PresignedUrlPort;

	@Override
	public PresignedUrlResponseDTO createPresignedUrl(PresignedUrlCreateCommand command) {

		LocalDate now = LocalDate.now();
		String yyyy = now.format(DateTimeFormatter.ofPattern("yyyy"));
		String mm = now.format(DateTimeFormatter.ofPattern("MM"));


		String originalFileName = command.getFileName();
		String fileName = (originalFileName == null || originalFileName.isBlank()) ? "unknown" : originalFileName;

		String key = String.format("posts/%s/%s/%s-%s", yyyy, mm, UUID.randomUUID(), fileName);


		URL url = s3PresignedUrlPort.generatePresignedPutUrl(key, command.getContentType());
		Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

		return new PresignedUrlResponseDTO(url.toString(), "PUT", expiresAt);

	}
}
