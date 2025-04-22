package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record UserDetailResponseDTO(
        Long userId,
        String userName,
        String regionName,
        String introduction,
        Long followerCount,
        Long followingCount,
        boolean isFollowing,
        String profileImageUrl
) {

    public static UserDetailResponseDTO from(Long userId, String username, String regionName,String introduction,Long followerCount,Long followingCount, boolean isFollowing, int imageLevel) {
        ProfileImage profileImage = ProfileImage.fromLevel(imageLevel);

        String profileImageUrl = "/images/" + profileImage.getImage();

        return new UserDetailResponseDTO(
                userId,
                username,
                regionName,
                introduction,
                followerCount,
                followingCount,
                isFollowing,
                profileImageUrl
        );

}}
