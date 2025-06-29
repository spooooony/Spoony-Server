package com.spoony.spoony_server.adapter.dto.user.response;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record UserBlockResponseDTO (
        Long userId,
        String username,
        String regionName,
        boolean isBlocked,
        String profileImageUrl,
        boolean isMine
) {
    public static UserBlockResponseDTO of(Long currentUserId, Long userId, String username, String regionName, boolean isBlocked, int imageLevel) {
        ProfileImage profileImage = ProfileImage.fromLevel(imageLevel);
        String profileImageUrl = "https://www.spoony-prod.o-r.kr/profile-images/" + profileImage.getImage();
        boolean isMine = currentUserId.equals(userId); // 현재 유저와 같은지 비교

        return new UserBlockResponseDTO(
                userId,
                username,
                regionName,
                isBlocked,
                profileImageUrl,
                isMine
        );
    }
}
