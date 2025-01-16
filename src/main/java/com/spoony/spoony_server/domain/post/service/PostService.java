package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.CategoryErrorMessage;
import com.spoony.spoony_server.common.message.SpoonErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.dto.request.PostCreateRequestDTO;
import com.spoony.spoony_server.domain.post.entity.*;
import com.spoony.spoony_server.domain.post.repository.*;
import com.spoony.spoony_server.domain.spoon.entity.ActivityEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonBalanceEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonHistoryEntity;
import com.spoony.spoony_server.domain.spoon.repository.ActivityRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonBalanceRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonHistoryRepository;
import com.spoony.spoony_server.domain.user.entity.FollowEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PostRepository postRepository;
    private final MenuRepository menuRepository;
    private final PhotoRepository photoRepository;
    private final CategoryRepository categoryRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final ActivityRepository activityRepository;
    private final ZzimPostRepository zzimPostRepository;
    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;

    @Transactional
    public void createPost(PostCreateRequestDTO postCreateRequestDTO) {

        // 게시글 업로드
        UserEntity userEntity = userRepository.findById(postCreateRequestDTO.userId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));

        CategoryEntity categoryEntity = categoryRepository.findById(postCreateRequestDTO.categoryId())
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.NOT_FOUND_ERROR));

        PlaceEntity placeEntity = PlaceEntity.builder()
                .placeName(postCreateRequestDTO.placeName())
                .placeAddress(postCreateRequestDTO.placeAddress())
                .placeRoadAddress(postCreateRequestDTO.placeRoadAddress())
                .latitude(postCreateRequestDTO.latitude())
                .longitude(postCreateRequestDTO.longitude())
                .build();

        placeRepository.save(placeEntity);

        PostEntity postEntity = PostEntity.builder()
                .user(userEntity)
                .place(placeEntity)
                .title(postCreateRequestDTO.title())
                .description(postCreateRequestDTO.description())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(postEntity);

        PostCategoryEntity postCategoryEntity = PostCategoryEntity.builder()
                .post(postEntity)
                .category(categoryEntity)
                .build();

        postCategoryRepository.save(postCategoryEntity);

        postCreateRequestDTO.menuList().stream()
                .map(menuName -> MenuEntity.builder()
                        .post(postEntity)
                        .menuName(menuName)
                        .build())
                .forEach(menuRepository::save);

        PhotoEntity photoEntity = PhotoEntity.builder()
                .post(postEntity)
                .photoUrl(postCreateRequestDTO.photo())
                .build();

        photoRepository.save(photoEntity);

        // 작성자 스푼 개수 조정
        ActivityEntity activityEntity = activityRepository.findById(2L)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));

        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser(userEntity)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));

        spoonBalanceEntity.setAmount(spoonBalanceEntity.getAmount() + activityEntity.getChangeAmount());
        spoonBalanceEntity.setUpdatedAt(LocalDateTime.now());

        spoonBalanceRepository.save(spoonBalanceEntity);

        // 스푼 히스토리 기록
        SpoonHistoryEntity spoonHistoryEntity = SpoonHistoryEntity.builder()
                .user(userEntity)
                .activity(activityEntity)
                .balanceAfter(spoonBalanceEntity.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        spoonHistoryRepository.save(spoonHistoryEntity);

        // 작성자 지도 리스트에 게시물 추가
        ZzimPostEntity zzimPostEntity = ZzimPostEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .build();

        zzimPostRepository.save(zzimPostEntity);

        // 작성자를 팔로우하는 사용자들의 피드에 게시물 추가
        List<FollowEntity> followerList = followRepository.findByFollowing(userEntity);

        List<FeedEntity> feedList = followerList.stream()
                .map(follower -> FeedEntity.builder()
                        .user(follower.getFollower())
                        .post(postEntity)
                        .build())
                .toList();

        feedRepository.saveAll(feedList);
    }
}
