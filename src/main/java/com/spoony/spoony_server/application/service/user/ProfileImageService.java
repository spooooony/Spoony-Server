package com.spoony.spoony_server.application.service.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spoony.spoony_server.adapter.dto.user.response.ProfileImageListResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.response.ProfileImageResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.user.ProfileImageGetUseCase;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UnlockedProfileImagePort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.ProfileImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileImageService implements ProfileImageGetUseCase {

	private final PostPort postPort;
	private final UnlockedProfileImagePort unlockedProfileImagePort;

	@Override
	@Transactional
	public ProfileImageListResponseDTO getAvailableProfileImages(UserGetCommand command) {
		Long userId = command.getUserId();

		// 1. 현재 총 찜 개수 계산
		List<Post> postList = postPort.findPostsByUserId(userId);
		long totalZzimCount = postList.stream()
			.mapToLong(Post::getZzimCount)
			.sum();

		// 2. 이미 잠금해제된 레벨 조회
		Set<Integer> unlockedLevels = unlockedProfileImagePort
			.findUnlockedLevelsByUserId(userId);

		List<ProfileImageResponseDTO> result = new ArrayList<>();

		// 3. 각 레벨별로 확인 및 새로운 잠금해제 처리
		for (ProfileImage profileImage : ProfileImage.values()) {
			int level = profileImage.getImageLevel();
			boolean wasUnlocked = unlockedLevels.contains(level);
			boolean canUnlock = totalZzimCount >= profileImage.getRequiredZzimCount();

			// 새로 잠금해제 조건 달성 시 저장
			if (!wasUnlocked && canUnlock) {
				unlockedProfileImagePort.saveUnlockedLevel(userId, level);
				result.add(ProfileImageResponseDTO.of(profileImage, true));
			}
			// 이미 잠금해제된 경우 (한번 해제되면 영구 해제)
			else if (wasUnlocked) {
				result.add(ProfileImageResponseDTO.of(profileImage, true));
			}
			// 잠금 상태
			else {
				result.add(ProfileImageResponseDTO.of(profileImage, false));
			}
		}

		return ProfileImageListResponseDTO.of(result);
	}
}
