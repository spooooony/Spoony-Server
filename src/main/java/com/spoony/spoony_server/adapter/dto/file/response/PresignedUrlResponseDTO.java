package com.spoony.spoony_server.adapter.dto.file.response;

import java.time.Instant;

public record PresignedUrlResponseDTO(String url, String method, Instant expiresAt) {
}
