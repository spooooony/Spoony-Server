package com.spoony.spoony_server.domain.user.dto.response;

import java.time.LocalDateTime;

public record UserResponseDTO(Long userId,
                              String userEmail,
                              String userName,
                              String userImageUrl,
                              String regionName,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
