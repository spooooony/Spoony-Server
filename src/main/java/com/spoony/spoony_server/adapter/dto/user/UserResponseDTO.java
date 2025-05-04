package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.ProfileImage;

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

    // UserSimpleResponseDTO 생성시 profileImageUrl을 설정
    public static UserResponseDTO from(
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
        String profileImageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();

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