package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record  UserSimpleResponseDTO (
    Long userId,
    String username,
    String regionName,
    boolean isFollowing,
    String profileImageUrl
){

    public static UserSimpleResponseDTO from(Long userId, String username, String regionName, boolean isFollowing, int imageLevel) {
        ProfileImage profileImage = ProfileImage.fromLevel(imageLevel);
        String profileImageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();

        return new UserSimpleResponseDTO(
                userId,
                username,
                regionName,
                isFollowing,
                profileImageUrl
        );
}}
