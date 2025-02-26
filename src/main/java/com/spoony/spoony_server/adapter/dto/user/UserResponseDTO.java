package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.Provider;

import java.time.LocalDateTime;

public record UserResponseDTO(long userId,
                              Provider provider,
                              String providerId,
                              String userName,
                              String userImageUrl,
                              String regionName,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
