package com.spoony.spoony_server.adapter.dto.file.request;

public record PresignedUrlRequestDTO(String fileName,
									 String contentType) {
}
