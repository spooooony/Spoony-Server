package com.spoony.spoony_server.adapter.dto.user.response;

import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.ProfileImage;
import com.spoony.spoony_server.global.support.ProfileCdn;

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
                              Long reviewCount,String profileImageUrl) {

    public static UserResponseDTO of(
            long userId,
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
            Long reviewCount,
            int profileImageLevel
    ) {
        ProfileImage profileImage = ProfileImage.fromLevel(profileImageLevel);
        String profileImageUrl = ProfileCdn.url(profileImage.getImage());

        return new UserResponseDTO(
                userId,
                platform,
                platformId,
                userName,
                regionName,
                introduction,
                createdAt,
                updatedAt,
                followerCount,
                followingCount,
                isFollowing,
                reviewCount,
                profileImageUrl
        );
    }
}
