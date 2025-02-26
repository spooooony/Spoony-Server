package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.Platform;

import java.time.LocalDateTime;

public record UserResponseDTO(long userId,
                              Platform platform,
                              String platformId,
                              String userName,
                              String userImageUrl,
                              String regionName,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
