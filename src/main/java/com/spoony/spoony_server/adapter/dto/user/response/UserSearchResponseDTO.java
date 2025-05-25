package com.spoony.spoony_server.adapter.dto.user.response;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record UserSearchResponseDTO(
        Long userId,
        String username,
        String regionName,
        String profileImageUrl
) {

    public static UserSearchResponseDTO of(Long userId, String username, String regionName, int imageLevel) {
        ProfileImage profileImage = ProfileImage.fromLevel(imageLevel);
        String profileImageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();

        return new UserSearchResponseDTO(
                userId,
                username,
                regionName,
                profileImageUrl
        );
    }
}
