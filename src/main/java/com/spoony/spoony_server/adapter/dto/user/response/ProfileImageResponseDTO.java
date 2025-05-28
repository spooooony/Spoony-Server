package com.spoony.spoony_server.adapter.dto.user.response;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record ProfileImageResponseDTO(
        int imageLevel,
        String spoonName,
        String unlockCondition,
        String imageUrl,
        boolean isUnlocked
) {
    public static ProfileImageResponseDTO of(ProfileImage profileImage, boolean isUnlocked) {
        String imageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();
        return new ProfileImageResponseDTO(
                profileImage.getImageLevel(),
                profileImage.getSpoonName(),
                profileImage.getUnlockCondition(),
                imageUrl,
                isUnlocked
        );
    }
}