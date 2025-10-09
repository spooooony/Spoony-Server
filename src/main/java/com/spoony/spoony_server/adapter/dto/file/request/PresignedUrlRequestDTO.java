package com.spoony.spoony_server.adapter.dto.file.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PresignedUrlRequestDTO(
	@NotBlank(message = "파일명은 필수 값입니다.")
	@Size(max = 255, message = "파일명은 255자를 초과할 수 없습니다.")
	@Pattern(regexp = "^[^./\\\\][^/\\\\]*$", message = "파일명에 경로 구분자를 포함할 수 없습니다.")
	String fileName,
	@Pattern(regexp = "^image/(jpeg|png|gif|webp)$", message = "지원되지 않는 이미지 타입입니다.")
	String contentType) {
}

