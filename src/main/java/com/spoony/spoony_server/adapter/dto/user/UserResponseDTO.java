package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.Platform;

import java.time.LocalDateTime;

public record UserResponseDTO(long userId,
                              Platform platform,
                              String platformId,
                              String userName,
                              String regionName,
                              String introduction,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Long followerCount,
                              Long followingCount,
                              boolean isFollowing,
                              Long reviewCount) {
}
