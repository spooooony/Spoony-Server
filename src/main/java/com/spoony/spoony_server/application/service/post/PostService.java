package com.spoony.spoony_server.application.service.post;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.application.port.command.post.PostCreateCommand;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostPhotoSaveCommand;
import com.spoony.spoony_server.application.port.in.post.PostCreateUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetCategoriesUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.post.PostScoopPostUseCase;
import com.spoony.spoony_server.application.port.out.post.PostCreatePort;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.CategoryErrorMessage;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.dto.post.PostCreateDTO;
import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.CategoryMonoListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.CategoryMonoResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.CategoryType;
import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ActivityEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonHistoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ActivityRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ScoopPostRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonHistoryRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements
        PostGetUseCase,
        PostCreateUseCase,
        PostGetCategoriesUseCase,
        PostScoopPostUseCase {

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

    private final PostCreatePort postCreatePort;

    @Transactional
    public PostResponseDTO getPostById(PostGetCommand command) {

        PostEntity postEntity = postRepository.findById(command.getPostId()).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(command.getUserId()).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));
        Long categoryId = postCategoryEntity.getCategory().getCategoryId();
        CategoryEntity categoryEntity = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));

        PlaceEntity place = postEntity.getPlace();
        LocalDateTime latestDate = postEntity.getUpdatedAt().isAfter(postEntity.getCreatedAt()) ? postEntity.getUpdatedAt() : postEntity.getCreatedAt();

        Long zzimCount = zzimPostRepository.countByPost(postEntity);
        Boolean isMine = postEntity.getUser().getUserId().equals(command.getUserId());
        Boolean isZzim = zzimPostRepository.existsByUserAndPost(userEntity, postEntity);
        Boolean isScoop = scoopPostRepository.existsByUserAndPost(userEntity, postEntity);
        List<PhotoEntity> photoEntityList = photoRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

        List<MenuEntity> menuEntityList = menuRepository.findByPost(postEntity)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.MENU_NOT_FOUND));

        List<String> menuList = menuEntityList.stream()
                .map(menuEntity -> menuEntity.getMenuName())
                .collect(Collectors.toList());

        List<String> photoUrlList = photoEntityList.stream()
                .map(PhotoEntity::getPhotoUrl)
                .collect(Collectors.toList());

        return new PostResponseDTO(command.getPostId(),
                postEntity.getUser().getUserId(),
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
                isMine,
                isZzim,
                isScoop,
                new CategoryColorResponseDTO(
                        categoryEntity.getCategoryId(),
                        categoryEntity.getCategoryName(),
                        categoryEntity.getIconUrlColor(),
                        categoryEntity.getTextColor(),
                        categoryEntity.getBackgroundColor())
        );
    }

    public List<String> savePostImages(PostPhotoSaveCommand photoSaveCommand) throws IOException {
        List<String> photoUrlList = postCreatePort.savePostImages(photoSaveCommand.getPhotos());
        return photoUrlList;
    }

    @Transactional
    public void createPost(PostCreateCommand command) {
        // 게시글 업로드
        UserEntity userEntity = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        CategoryEntity categoryEntity = categoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));

        PlaceEntity placeEntity = PlaceEntity.builder()
                .placeName(command.getPlaceName())
                .placeAddress(command.getPlaceAddress())
                .placeRoadAddress(command.getPlaceRoadAddress())
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .build();

        placeRepository.save(placeEntity);

        PostEntity postEntity = PostEntity.builder()
                .user(userEntity)
                .place(placeEntity)
                .title(command.getTitle())
                .description(command.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(postEntity);

        PostCategoryEntity postCategoryEntity = PostCategoryEntity.builder()
                .post(postEntity)
                .category(categoryEntity)
                .build();

        postCategoryRepository.save(postCategoryEntity);

        command.getMenuList().stream()
                .map(menuName -> MenuEntity.builder()
                        .post(postEntity)
                        .menuName(menuName)
                        .build())
                .forEach(menuRepository::save);

        command.getPhotoUrlList().stream()
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
    public void scoopPost(ScoopPostRequestDTO scoopPostRequestDTO) {

        Long postId = scoopPostRequestDTO.postId();
        Long userId = scoopPostRequestDTO.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));


        //현재 사용자의 스푼 개수 조회
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser(userEntity).orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));


        //스푼 잔액이 1개 미만이면 에러 발생

        if (spoonBalanceEntity.getAmount() < 1) {
            throw new BusinessException(SpoonErrorMessage.NOT_ENOUGH_SPOONS);
        }
        //떠먹은 포스트에 반영
        ScoopPostEntity scoopPostEntity = ScoopPostEntity.builder().user(userEntity).post(postEntity).build();
        scoopPostRepository.save(scoopPostEntity);

        // 작성자 스푼 개수 조정
        ActivityEntity activityEntity = activityRepository.findById(3L)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));


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
    }
}
