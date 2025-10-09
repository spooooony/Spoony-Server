package com.spoony.spoony_server.adapter.in.web.file;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spoony.spoony_server.adapter.dto.file.request.PresignedUrlRequestDTO;
import com.spoony.spoony_server.adapter.dto.file.response.PresignedUrlResponseDTO;
import com.spoony.spoony_server.application.port.command.file.PresignedUrlCreateCommand;
import com.spoony.spoony_server.application.port.in.file.PresignedUrlCreateUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;

import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Image Upload", description = "이미지 업로드용 Presigned URL 발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageUploadController {
	private final PresignedUrlCreateUseCase presignedUrlCreateUseCase;

	@PostMapping("/presigned")
	@Operation(summary = "Presigned URL 발급", description = "클라이언트가 직접 S3로 이미지를 업로드할 수 있도록 Presigned URL을 발급합니다.")
	public ResponseEntity<ResponseDTO<PresignedUrlResponseDTO>> generatePresignedUrl(@RequestBody
		PresignedUrlRequestDTO request) {
		PresignedUrlCreateCommand command = new PresignedUrlCreateCommand(request.fileName(),
			request.contentType());

		PresignedUrlResponseDTO response = presignedUrlCreateUseCase.createPresignedUrl(command);

		return ResponseEntity.ok(ResponseDTO.success(response));

	}

}
