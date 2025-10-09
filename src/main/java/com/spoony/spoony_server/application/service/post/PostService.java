package com.spoony.spoony_server.application.service.post;

import com.spoony.spoony_server.adapter.dto.post.response.*;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import com.spoony.spoony_server.application.port.command.post.*;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserReviewGetCommand;
import com.spoony.spoony_server.application.port.in.post.*;
import com.spoony.spoony_server.application.port.out.file.S3DeletePort;
import com.spoony.spoony_server.application.port.out.file.S3PresignedUrlPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.application.port.out.post.*;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import com.spoony.spoony_server.application.port.out.user.RegionPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.Region;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.event.AfterCommitWrapper;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements
        PostGetUseCase,
        PostCreateUseCase,
        PostGetCategoriesUseCase,
        PostScoopPostUseCase,
        PostDeleteUseCase,
        PostUpdateUseCase,
        PostSearchUseCase {
    private final PostPort postPort;
    private final PostCategoryPort postCategoryPort;
    private final CategoryPort categoryPort;
    private final UserPort userPort;
    private final ZzimPostPort zzimPostPort;
    private final PlacePort placePort;
    private final SpoonPort spoonPort;
    //private final PostDeletePort postDeletePort;

    private final PhotoPort photoPort;
    private final BlockPort blockPort;
    private final ReportPort reportPort;
    private final RegionPort regionPort;
    private final ApplicationEventPublisher eventPublisher;
    private final S3DeletePort s3DeletePort;
    @Transactional
    public PostResponseDTO getPostById(PostGetCommand command) {

        Post post = postPort.findPostById(command.getPostId());
        User user = userPort.findUserById(command.getUserId());

        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
        Long categoryId = postCategory.getCategory().getCategoryId();
        Category category = categoryPort.findCategoryById(categoryId);
        Place place = post.getPlace();

        Long zzimCount = post.getZzimCount();
        boolean isMine = post.getUser().getUserId().equals(command.getUserId());
        boolean isZzim = zzimPostPort.existsByUserIdAndPostId(user.getUserId(), post.getPostId());
        boolean isScoop = postPort.existsByUserIdAndPostId(user.getUserId(), post.getPostId());

        List<Photo> photoList = postPort.findPhotoById(post.getPostId());
        List<String> photoUrlList = photoList.stream()
                .map(Photo::getPhotoUrl)
                .toList();

        List<Menu> menuList = postPort.findMenuById(post.getPostId());
        List<String> menuNameList = menuList.stream()
                .map(Menu::getMenuName)
                .toList();

        return PostResponseDTO.of(command.getPostId(),
                post.getUser().getUserId(),
                photoUrlList,
                post.getCreatedAt(),
                menuNameList,
                post.getDescription(),
                post.getValue(),
                post.getCons(),
                place.getPlaceName(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                zzimCount,
                isMine,
                isZzim,
                isScoop,
                CategoryColorResponseDTO.of(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlColor(),
                        category.getTextColor(),
                        category.getBackgroundColor())
        );
    }

    //유저페이지 -> 유저가 작성한 리뷰 조회 시 사용
    public FeedListResponseDTO getPostsByUserId(UserReviewGetCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();
        Boolean isLocalReview = command.getIsLocalReview();

        List<Post> postList = postPort.findPostsByUserId(targetUserId);
        List<Long> reportedPostIds = postPort.getReportedPostIds(userId);

        postList = postList.stream()
                .filter(post -> {
                    boolean isReported = reportedPostIds.contains(post.getPostId());
                    return !isReported;
                })
                .collect(Collectors.toList());

        // localReview = true: 활동 지역과 식당 지역이 같은 게시물만 필터링
        if (Boolean.TRUE.equals(isLocalReview)) {
            postList = postList.stream()
                    .filter(post -> {
                        Region region = post.getUser().getRegion();
                        if (region == null) return false;

                        String regionName = region.getRegionName();
                        if (regionName == null) return false;

                        String placeAddress = post.getPlace().getPlaceAddress();
                        return placeAddress != null && placeAddress.contains(regionName);
                    })
                    .toList();
        }

        // 날짜 기준 최신순 정렬 추가
        postList = new ArrayList<>(postList);
        postList.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        //3. 각 Post -> Feed
        List<FeedResponseDTO> feedResponseList = postList.stream().map(post -> {
                    User author = post.getUser();
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = postCategory.getCategory();
                    boolean isMine = author.getUserId().equals(command.getUserId());
                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    String regionName = author.getRegion() != null ? author.getRegion().getRegionName() : null;

                    return FeedResponseDTO.of(
                            author.getUserId(),
                            author.getUserName(),
                            regionName,
                            post.getPostId(),
                            post.getDescription(),
                            CategoryColorResponseDTO.of(category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()

                            ),
                            post.getZzimCount(),
                            photoUrlList,
                            post.getCreatedAt(),
                            isMine
                    );
                })
                .collect(Collectors.toList());
        return FeedListResponseDTO.of(feedResponseList);
    }

    @Override
    public PostSearchResultListDTO searchReviewsByQuery(UserGetCommand userGetCommand, PostSearchCommand postSearchCommand) {
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userGetCommand.getUserId());
        List<Long> blockingUserIds = blockPort.getBlockerUserIds(userGetCommand.getUserId());
        List<Long> reportedPostIds = reportPort.findReportedPostIdsByUserId(userGetCommand.getUserId());

        List<Post> postList = postPort.findByPostDescriptionContaining(postSearchCommand.getQuery());

        List<FeedResponseDTO> postSearchResultList = postList.stream()
                .filter(post -> !blockedUserIds.contains(post.getUser().getUserId()) &&
                        !blockingUserIds.contains(post.getUser().getUserId()) &&
                        !reportedPostIds.contains(post.getPostId()))
                .map(post -> {
                    List<String> photoUrlList = postPort.findPhotoById(post.getPostId()).stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = categoryPort.findCategoryById(postCategory.getCategory().getCategoryId());
                    Long zzimCount = post.getZzimCount();
                    String regionName = post.getUser().getRegion() != null ? post.getUser().getRegion().getRegionName() : null;
                    boolean isMine = post.getUser().getUserId().equals(userGetCommand.getUserId());

                    return FeedResponseDTO.of(
                            post.getUser().getUserId(),
                            post.getUser().getUserName(),
                            regionName,
                            post.getPostId(),
                            post.getDescription(),
                            CategoryColorResponseDTO.of(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ),
                            zzimCount,
                            photoUrlList,
                            post.getCreatedAt(),
                            isMine
                    );
                })
                .toList();

        return PostSearchResultListDTO.of(postSearchResultList);
    }


    @Transactional
    public PostCreatedEvent createPost(PostCreateCommand command) {
        // 게시글 업로드
        User user = userPort.findUserById(command.getUserId());
        Category category = categoryPort.findCategoryById(command.getCategoryId());

        Place place = placePort.findByPlaceNameAndCoordinates(
                command.getPlaceName(),
                command.getLatitude(),
                command.getLongitude()
        );

        if (place == null) {
            // 없으면 새로 생성
            Region region = regionPort.findByAddress(command.getPlaceAddress());

            place = new Place(
                    command.getPlaceName(),
                    command.getPlaceAddress(),
                    command.getPlaceRoadAddress(),
                    command.getLatitude(),
                    command.getLongitude(),
                    region
            );
            Long placeId = placePort.savePlace(place);
            place = placePort.findPlaceById(placeId);
        }

        try {
            Post post = new Post(
                    user,
                    place,
                    command.getDescription(),
                    command.getValue(),
                    command.getCons(),
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            Long postId = postPort.savePost(post);
            post = postPort.findPostById(postId);

            PostCategory postCategory = new PostCategory(post, category);

            postPort.savePostCategory(postCategory);

            Post finalPost = post;
            command.getMenuList().forEach(menuName -> {
                Menu menu = new Menu(finalPost, menuName);
                postPort.saveMenu(menu);
            });

            command.getPhotoUrlList().forEach(photoUrl -> {
                Photo photo = new Photo(finalPost, photoUrl);
                postPort.savePhoto(photo);
            });

            // 작성자 지도 리스트에 게시물 추가
            zzimPostPort.saveZzimPost(user, post);

            // 작성자를 팔로우하는 사용자들의 피드에 게시물 추가
            List<Follow> followList = userPort.findFollowersByUserId(user.getUserId());
            List<Long> followerIds = followList.stream().map(follow -> follow.getFollower().getUserId()).toList();

            PostCreatedEvent event = new PostCreatedEvent(this, followerIds, post.getPostId());
            eventPublisher.publishEvent(new AfterCommitWrapper(event));
            return event;
        } catch (DataIntegrityViolationException e) {
            Long existingPostId = postPort
                    .findPostIdByUserAndPlace(user.getUserId(), place.getPlaceId())
                    .orElseThrow(() -> new BusinessException(PostErrorMessage.ALREADY_CREATED));

            return new PostCreatedEvent(this, List.of(), existingPostId);
        }
    }

    // 모든 카테고리 조회
    @Transactional
    public CategoryMonoListResponseDTO getAllCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryPort.findAllCategories().stream()
                .map(category -> CategoryMonoResponseDTO.of(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .toList();

        return CategoryMonoListResponseDTO.of(categoryMonoResponseDTOList);
    }

    // 음식 카테고리 조회
    @Transactional
    public CategoryMonoListResponseDTO getFoodCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryPort.findFoodCategories().stream()
                .map(category -> CategoryMonoResponseDTO.of(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .toList();

        return CategoryMonoListResponseDTO.of(categoryMonoResponseDTOList);
    }

    @Transactional
    public void scoopPost(PostScoopPostCommand command) {
        Long userId = command.getUserId();
        Long postId = command.getPostId();

        User user = userPort.findUserById(userId);

        // 신규 삽입 시에만 후속 단계 진행
        boolean inserted = postPort.insertScoopIfAbsent(userId, postId);
        if (!inserted) {
            throw new BusinessException(SpoonErrorMessage.ALREADY_SCOOPED);
        }

        // 스푼 조건부 차감
        boolean decrement = spoonPort.decrementIfEnough(userId, 1);
        if (!decrement) {
            postPort.deleteScoop(userId, postId);
            throw new BusinessException(SpoonErrorMessage.NOT_ENOUGH_SPOONS);
        }

        // 스푼 히스토리 저장
        Activity activity = spoonPort.findActivityByActivityId(3L);
        spoonPort.updateSpoonHistoryByActivity(user, activity);
    }

    @Transactional
    public void deletePost(PostDeleteCommand command) {

        Long postId = command.getPostId();
        //S3 삭제 로직
        List<String> imageUrls = photoPort.getPhotoUrls(command.getPostId());

        try {
            s3DeletePort.deleteImagesFromS3(imageUrls);
        } catch (Exception e) {
            // S3 삭제 실패는 Business Exception으로 처리하여 DB 트랜잭션 롤백 유도
            log.error("S3 파일 삭제 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(PostErrorMessage.S3_DELETE_FAILED);
        }

        postPort.deleteById(command.getPostId());
    }

    // @Transactional
    // public void updatePost(PostUpdateCommand command) {
    //     //S3 삭제 로직
    //     List<String> deletePhotoUrlList = command.getDeletePhotoUrlList();
    //
    //     if (!deletePhotoUrlList.isEmpty()) {
    //     s3DeletePort.deleteImagesFromS3(deletePhotoUrlList);
    //
    //     postPort.updatePost(command.getPostId(), command.getDescription(), command.getValue(), command.getCons());
    //     Post post = postPort.findPostById(command.getPostId());
    //
    //     postPort.deleteAllPostCategoryByPostId(command.getPostId());
    //     postPort.deleteAllMenusByPostId(command.getPostId());
    //     postPort.deleteAllPhotosByPhotoUrl(deletePhotoUrlList);
    //
    //     Category category = categoryPort.findCategoryById(command.getCategoryId());
    //     PostCategory postCategory = new PostCategory(post, category);
    //     postPort.savePostCategory(postCategory);
    //
    //     command.getMenuList().forEach(menuName -> {
    //         Menu menu = new Menu(post, menuName);
    //         postPort.saveMenu(menu);
    //     });
    //
    //     command.getPhotoUrlList().forEach(photoUrl -> {
    //         Photo photo = new Photo(post, photoUrl);
    //         postPort.savePhoto(photo);
    //     });
    // }
    @Transactional
    public void updatePost(PostUpdateCommand command) {
        List<String> deletePhotoUrlList = command.getDeletePhotoUrlList();

        if (!deletePhotoUrlList.isEmpty()) {
            // 1. S3 삭제
            s3DeletePort.deleteImagesFromS3(deletePhotoUrlList);

            // 2. DB Photo 엔티티 삭제
            // postPort에 구현된 메서드가 맞다면 이대로 유지.
            // Photo 관련 책임은 PhotoPort로 분리하는 것이 이상적입니다.
            postPort.deleteAllPhotosByPhotoUrl(deletePhotoUrlList);
        }

        // 3. Post 내용 업데이트
        postPort.updatePost(command.getPostId(), command.getDescription(), command.getValue(), command.getCons());
        Post post = postPort.findPostById(command.getPostId());

        // 4. 기존 카테고리/메뉴 정보 삭제 (CascadeType.ALL 대신 수동 삭제를 선택한 경우 유지)
        // 만약 Cascade를 활용하여 업데이트한다면 이 코드는 제거하고,
        // post.getMenus().clear(); 후 새 메뉴를 추가해야 합니다.
        // 현재는 수동 삭제 방식을 유지합니다.
        postPort.deleteAllPostCategoryByPostId(command.getPostId());
        postPort.deleteAllMenusByPostId(command.getPostId());

        // (삭제된 Photo는 위에서 처리했으므로 여기서 다시 호출하면 안 됩니다.)

        // 5. 새로운 카테고리/메뉴/사진 정보 저장 (기존 로직 유지)
        Category category = categoryPort.findCategoryById(command.getCategoryId());
        postPort.savePostCategory(new PostCategory(post, category));

        command.getMenuList().forEach(menuName -> {
            Menu menu = new Menu(post, menuName);
            postPort.saveMenu(menu);
        });

        command.getPhotoUrlList().forEach(photoUrl -> {
            Photo photo = new Photo(post, photoUrl);
            postPort.savePhoto(photo);
        });
    }

    // public void deletePhotos(PostPhotoDeleteCommand command) {
    //     List<String> imageUrls = command.getDeleteImageUrlList();
    //     s3DeletePort.deleteImagesFromS3(imageUrls);
    // }
}
