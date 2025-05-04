package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.ProfileImage;

public record ProfileImageResponseDTO(
        int imageLevel,
        String unlockCondition,
        String imageUrl,
        boolean isUnlocked
) {
    public static ProfileImageResponseDTO of(ProfileImage profileImage, boolean isUnlocked) {
        String imageUrl = "https://www.spoony.o-r.kr/profile-images/" + profileImage.getImage();
        System.out.println("Generated Image URL: " + imageUrl);  // 이 부분에서 URL을 콘솔에 출력
        return new ProfileImageResponseDTO(
                profileImage.getImageLevel(),
                profileImage.getUnlockCondition(),
                imageUrl,
                isUnlocked
        );
    }
}