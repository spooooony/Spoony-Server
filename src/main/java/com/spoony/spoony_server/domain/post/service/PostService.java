package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.CategoryErrorMessage;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.SpoonErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.dto.request.PostCreateRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.CategoryMonoDTO;
import com.spoony.spoony_server.domain.post.dto.PostCreateDTO;
import com.spoony.spoony_server.domain.post.dto.response.PostResponseDTO;
import com.spoony.spoony_server.domain.post.entity.*;
import com.spoony.spoony_server.domain.post.repository.*;
import com.spoony.spoony_server.domain.spoon.entity.ActivityEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonBalanceEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonHistoryEntity;
import com.spoony.spoony_server.domain.spoon.repository.ActivityRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonBalanceRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonHistoryRepository;
import com.spoony.spoony_server.domain.user.entity.FollowEntity;
import com.spoony.spoony_server.domain.user.entity.RegionEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import com.spoony.spoony_server.domain.post.enums.CategoryType;
import com.spoony.spoony_server.infra.service.AwsFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final ActivityRepository activityRepository;
    private final ZzimPostRepository zzimPostRepository;
    private final FollowRepository followRepository;
    private final FeedRepository feedRepository;

    private final AwsFileService awsFileService;

    @Transactional
    public PostResponseDTO getPostById(Long postId) {

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        UserEntity userEntity = postEntity.getUser();

        if (userEntity == null) {
            throw new BusinessException(PostErrorMessage.NOT_FOUND_ERROR);
        }

        RegionEntity regionEntity = userRepository.findReigonByUserId(userEntity.getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));
        PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        Long categoryId = postCategoryEntity.getCategory().getCategoryId();
        CategoryEntity categoryEntity = categoryRepository.findByCategoryId(categoryId);

        PlaceEntity place = postEntity.getPlace();
        LocalDateTime latestDate = postEntity.getUpdatedAt().isAfter(postEntity.getCreatedAt()) ? postEntity.getUpdatedAt() : postEntity.getCreatedAt();
        String formattedDate = latestDate.toLocalDate().toString();
        Long zzimCount = postRepository.countByPostId(postId);
        String category = categoryEntity.getCategoryName();

        List<MenuEntity> menuEntityList = menuRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));

        List<String> menuList = menuEntityList.stream()
                .map(menuEntity -> menuEntity.getMenuName())
                .collect(Collectors.toList());

        return new PostResponseDTO(
                postId,
                userEntity.getUserId(),
                userEntity.getUserName(),
                regionEntity.getRegionName(),
                category,
                postEntity.getTitle(),
                formattedDate,
                menuList,
                postEntity.getDescription(),
                place.getPlaceName(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                zzimCount
        );
    }

    @Transactional
    public void createPost(PostCreateDTO postCreateDTO) throws IOException {

        // 게시글 업로드
        UserEntity userEntity = userRepository.findById(postCreateDTO.userId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));

        CategoryEntity categoryEntity = categoryRepository.findById(postCreateDTO.categoryId())
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.NOT_FOUND_ERROR));

        PlaceEntity placeEntity = PlaceEntity.builder()
                .placeName(postCreateDTO.placeName())
                .placeAddress(postCreateDTO.placeAddress())
                .placeRoadAddress(postCreateDTO.placeRoadAddress())
                .latitude(postCreateDTO.latitude())
                .longitude(postCreateDTO.longitude())
                .build();

        placeRepository.save(placeEntity);

        PostEntity postEntity = PostEntity.builder()
                .user(userEntity)
                .place(placeEntity)
                .title(postCreateDTO.title())
                .description(postCreateDTO.description())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(postEntity);

        PostCategoryEntity postCategoryEntity = PostCategoryEntity.builder()
                .post(postEntity)
                .category(categoryEntity)
                .build();

        postCategoryRepository.save(postCategoryEntity);

        postCreateDTO.menuList().stream()
                .map(menuName -> MenuEntity.builder()
                        .post(postEntity)
                        .menuName(menuName)
                        .build())
                .forEach(menuRepository::save);

        postCreateDTO.photoUrlList().stream()
                .map(photoUrl -> PhotoEntity.builder()
                        .post(postEntity)
                        .photoUrl(photoUrl)
                        .build())
                .forEach(photoRepository::save);

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

    // 모든 카테고리 조회
    public List<CategoryMonoDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryMonoDTO(
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .collect(Collectors.toList());
    }

    // 음식 카테고리 조회
    public List<CategoryMonoDTO> getFoodCategories() {
        return categoryRepository.findByCategoryType(CategoryType.FOOD).stream()
                .map(category -> new CategoryMonoDTO(
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .collect(Collectors.toList());
    }
}
