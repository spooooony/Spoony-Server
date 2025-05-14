package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record UserSearchResultDTO(
        Long userId,
        String username,
        String regionName,
        String profileImageUrl
) {

    public static UserSearchResultDTO from(Long userId, String username, String regionName, int imageLevel) {
        ProfileImage profileImage = ProfileImage.fromLevel(imageLevel);
        String profileImageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();

        return new UserSearchResultDTO(
                userId,
                username,
                regionName,
                profileImageUrl
        );
    }
}
