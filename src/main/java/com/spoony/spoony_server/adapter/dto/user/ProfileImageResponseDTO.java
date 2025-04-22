package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record ProfileImageResponseDTO(
        int imageLevel,
        String unlockCondition,
        String imageUrl,
        boolean isUnlocked
) {
    public static ProfileImageResponseDTO of(ProfileImage profileImage, boolean isUnlocked) {
        return new ProfileImageResponseDTO(
                profileImage.getImageLevel(),
                profileImage.getUnlockCondition(),
                "/profile-images/" + profileImage.getImage(),
                isUnlocked
        );
    }
}
