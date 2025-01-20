package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.CategoryErrorMessage;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.SpoonErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.dto.PostCreateDTO;
import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.CategoryMonoListResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.CategoryMonoResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.PostResponseDTO;
import com.spoony.spoony_server.domain.post.entity.*;
import com.spoony.spoony_server.domain.post.enums.CategoryType;
import com.spoony.spoony_server.domain.post.repository.*;
import com.spoony.spoony_server.domain.spoon.dto.request.ScoopPostRequestDTO;
import com.spoony.spoony_server.domain.spoon.entity.ActivityEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonBalanceEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonHistoryEntity;
import com.spoony.spoony_server.domain.spoon.repository.ActivityRepository;
import com.spoony.spoony_server.domain.spoon.repository.ScoopPostRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonBalanceRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonHistoryRepository;
import com.spoony.spoony_server.domain.user.entity.FollowEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import com.spoony.spoony_server.domain.zzim.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.zzim.repository.ZzimPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ScoopPostRepository scoopPostRepository;

    @Transactional
    public PostResponseDTO getPostById(Long postId, Long userId) {

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));
        Long categoryId = postCategoryEntity.getCategory().getCategoryId();
        CategoryEntity categoryEntity = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));

        PlaceEntity place = postEntity.getPlace();
        LocalDateTime latestDate = postEntity.getUpdatedAt().isAfter(postEntity.getCreatedAt()) ? postEntity.getUpdatedAt() : postEntity.getCreatedAt();

        Long zzimCount = zzimPostRepository.countByPost(postEntity);
        Boolean isZzim = zzimPostRepository.existsByUserAndPost(userEntity, postEntity);
        Boolean isScoop = scoopPostRepository.existsByUserAndPost(userEntity, postEntity);
        List<PhotoEntity> photoEntityList = photoRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

        String iconUrlColor = categoryEntity.getIconUrlColor();
        String backgroundColor = categoryEntity.getBackgroundColor();
        String textColor = categoryEntity.getTextColor();

        String category = categoryEntity.getCategoryName();

        List<MenuEntity> menuEntityList = menuRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.MENU_NOT_FOUND));

        List<String> menuList = menuEntityList.stream()
                .map(menuEntity -> menuEntity.getMenuName())
                .collect(Collectors.toList());

        List<String> photoUrlList = photoEntityList.stream()
                .map(PhotoEntity::getPhotoUrl)
                .collect(Collectors.toList());

        return new PostResponseDTO(postId,
                userEntity.getUserId(),
                photoUrlList,
                postEntity.getTitle(),
                latestDate,
                menuList,
                postEntity.getDescription(),
                place.getPlaceName(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                zzimCount,
                isZzim,
                isScoop,
                new CategoryColorResponseDTO(category, iconUrlColor, textColor, backgroundColor)
        );
    }

    @Transactional
    public void createPost(PostCreateDTO postCreateDTO) {

        // 게시글 업로드
        UserEntity userEntity = userRepository.findById(postCreateDTO.userId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        CategoryEntity categoryEntity = categoryRepository.findById(postCreateDTO.categoryId())
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));

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
    @Transactional
    public CategoryMonoListResponseDTO getAllCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryRepository.findAll().stream()
                .map(category -> new CategoryMonoResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .collect(Collectors.toList());

        return new CategoryMonoListResponseDTO(categoryMonoResponseDTOList);
    }

    // 음식 카테고리 조회
    @Transactional
    public CategoryMonoListResponseDTO getFoodCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryRepository.findByCategoryType(CategoryType.FOOD).stream()
                .map(category -> new CategoryMonoResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .collect(Collectors.toList());

        return new CategoryMonoListResponseDTO(categoryMonoResponseDTOList);
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> scoopPost(ScoopPostRequestDTO scoopPostRequestDTO) {

        Long postId = scoopPostRequestDTO.postId();
        Long userId = scoopPostRequestDTO.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        //떠먹은 포스트에 반영
        ScoopPostEntity scoopPostEntity = ScoopPostEntity.builder().user(userEntity).post(postEntity).build();
        scoopPostRepository.save(scoopPostEntity);

        // 작성자 스푼 개수 조정
        ActivityEntity activityEntity = activityRepository.findById(3L)
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

        // 사용자의 피드에서 게시물 삭제
        feedRepository.deleteByUserAndPost(userEntity, postEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
