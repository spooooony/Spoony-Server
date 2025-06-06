package com.spoony.spoony_server.application.service.post;

import com.spoony.spoony_server.adapter.dto.post.response.*;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import com.spoony.spoony_server.application.port.command.post.*;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserReviewGetCommand;
import com.spoony.spoony_server.application.port.in.post.*;
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
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.Region;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
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
        PostSearchUseCase
{
    private final PostPort postPort;
    private final PostCreatePort postCreatePort;
    private final PostCategoryPort postCategoryPort;
    private final CategoryPort categoryPort;
    private final UserPort userPort;
    private final ZzimPostPort zzimPostPort;
    private final PlacePort placePort;
    private final SpoonPort spoonPort;
    private final PostDeletePort postDeletePort;
    private final PhotoPort photoPort;
    private final BlockPort blockPort;
    private final ReportPort reportPort;
    private final RegionPort regionPort;

    @Transactional
    public PostResponseDTO getPostById(PostGetCommand command) {

        Post post = postPort.findPostById(command.getPostId());
        User user = userPort.findUserById(command.getUserId());

        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
        Long categoryId = postCategory.getCategory().getCategoryId();
        Category category = categoryPort.findCategoryById(categoryId);

        Place place = post.getPlace();
        LocalDateTime latestDate = post.getUpdatedAt().isAfter(post.getCreatedAt()) ? post.getUpdatedAt() : post.getCreatedAt();

        Long zzimCount = zzimPostPort.countZzimByPostId(post.getPostId()) - 1L;
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
                latestDate,
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
    public FeedListResponseDTO getPostsByUserId(UserReviewGetCommand command){
        Long userId = command.getUserId(); //나
        Long tagetUserId = command.getTargetUserId(); //타유저
        Boolean isLocalReview = command.getIsLocalReview();
        //1. 유저가 작성한 게시물 모두 조회
        List<Post> postList = postPort.findPostsByUserId(tagetUserId);

        //1-1. 타유저 페이지의 경우(userId != targetUserId), 타유저가 작성한 리뷰 중, 내가 신고한 리뷰는 필터링
        List<Long> reportedPostIds = postPort.getReportedPostIds(userId); //내가 신고한 게시물 ID들 조회
        System.out.println("🟢🟢🟢🟢reportedPostIds = " + reportedPostIds);

//        postList = postList.stream()
//                .filter(post -> !reportedPostIds.contains(post.getPostId()))
//                .collect(Collectors.toList());
        postList = postList.stream()
                .filter(post -> {
                    boolean isReported = reportedPostIds.contains(post.getPostId());
                    System.out.println("🟢🟢🟢🟢🟢Post ID: " + post.getPostId() +
                            " | 🟢🟢🟢Reported: " + isReported);
                    return !isReported;
                })
                .collect(Collectors.toList());


        //2. localReview가 true일 경우, 활동 지역과 식당 지역이 같은 게시물만 필터링
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
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt(),
                            isMine
                    );
                })
                .collect(Collectors.toList());
        return FeedListResponseDTO.of(feedResponseList);
    }

    @Override
    public ReviewAmountResponseDTO getPostAmountByUserId(UserGetCommand command) {
        Long userId = command.getUserId();
        Long reviewCount = postPort.countPostsByUserId(userId);
        return ReviewAmountResponseDTO.of(reviewCount);
    }

    @Override
    public PostSearchResultListDTO searchReviewsByQuery(UserGetCommand userGetCommand,PostSearchCommand postSearchCommand){
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userGetCommand.getUserId());
        List<Long> blockingUserIds = blockPort.getBlockerUserIds(userGetCommand.getUserId());
        List<Long> reportedPostIds = reportPort.findReportedPostIdsByUserId(userGetCommand.getUserId());

        List<Post> postList = postPort.findByPostDescriptionContaining(postSearchCommand.getQuery());

        List<FeedResponseDTO> postSearchResultList = postList.stream()
                .filter(post -> !blockedUserIds.contains(post.getUser().getUserId())&&
                !blockingUserIds.contains(post.getUser().getUserId()) &&
                !reportedPostIds.contains(post.getPostId()))
                .map(post -> {
                    List<String> photoUrlList = postPort.findPhotoById(post.getPostId()).stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = categoryPort.findCategoryById(postCategory.getCategory().getCategoryId());
                    Long zzimCount = zzimPostPort.countZzimByPostId(post.getPostId()) - 1L;
                    String regionName = post.getUser().getRegion() != null ? post.getUser().getRegion().getRegionName() : null;
                    boolean isMine = post.getUser().getUserId().equals(userGetCommand.getUserId());
                    LocalDateTime latestDate = post.getUpdatedAt().isAfter(post.getCreatedAt())
                            ? post.getUpdatedAt() : post.getCreatedAt();

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
                            latestDate,
                            isMine
                    );
                })
                .toList();

        return PostSearchResultListDTO.of(postSearchResultList);
    }

    public List<String> savePostImages(PostPhotoSaveCommand photoSaveCommand) throws IOException {
        List<String> photoUrlList = postCreatePort.savePostImages(photoSaveCommand.getPhotos());
        return photoUrlList;
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

        Post post = new Post(
                user,
                place,
                command.getDescription(),
                command.getValue(),
                command.getCons(),
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

        // 작성자 스푼 개수 조정
//        Activity activity = spoonPort.findActivityByActivityId(2L);
//        spoonPort.updateSpoonBalanceByActivity(user, activity);

        // 스푼 히스토리 기록
//        spoonPort.updateSpoonHistoryByActivity(user, activity);

        // 작성자 지도 리스트에 게시물 추가
        zzimPostPort.saveZzimPost(user, post);

        // 작성자를 팔로우하는 사용자들의 피드에 게시물 추가
        List<Follow> followList = userPort.findFollowersByUserId(user.getUserId());
        List<Long> followerIds = followList.stream().map(follow -> follow.getFollower().getUserId()).toList();

        return new PostCreatedEvent(this, followerIds, post.getPostId());
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

        User user = userPort.findUserById(command.getUserId());
        Post post = postPort.findPostById(command.getPostId());

        SpoonBalance spoonBalance = spoonPort.findBalanceByUserId(command.getUserId());

        //스푼 잔액이 1개 미만이면 에러 발생
        if (spoonBalance.getAmount() < 1) {
            throw new BusinessException(SpoonErrorMessage.NOT_ENOUGH_SPOONS);
        }

        //떠먹은 포스트에 반영
        postPort.saveScoopPost(user, post);

        // 작성자 스푼 개수 조정
        Activity activity = spoonPort.findActivityByActivityId(3L);
        spoonPort.updateSpoonBalanceByActivity(user, activity);

        // 스푼 히스토리 기록
        spoonPort.updateSpoonHistoryByActivity(user, activity);
    }

    @Transactional
    public void deletePost(PostDeleteCommand command) {
        //S3 삭제 로직
        List<String> imageUrls = photoPort.getPhotoUrls(command.getPostId());
        postDeletePort.deleteImagesFromS3(imageUrls);

        postPort.deleteById(command.getPostId());
    }

    @Transactional
    public void updatePost(PostUpdateCommand command) {
        //S3 삭제 로직
        List<String> deletePhotoUrlList = command.getDeletePhotoUrlList();
        postDeletePort.deleteImagesFromS3(deletePhotoUrlList);

        postPort.updatePost(command.getPostId(), command.getDescription(), command.getValue(), command.getCons());
        Post post = postPort.findPostById(command.getPostId());

        postPort.deleteAllPostCategoryByPostId(command.getPostId());
        postPort.deleteAllMenusByPostId(command.getPostId());
        postPort.deleteAllPhotosByPhotoUrl(deletePhotoUrlList);

        Category category = categoryPort.findCategoryById(command.getCategoryId());
        PostCategory postCategory = new PostCategory(post, category);
        postPort.savePostCategory(postCategory);

        command.getMenuList().forEach(menuName -> {
            Menu menu = new Menu(post, menuName);
            postPort.saveMenu(menu);
        });

        command.getPhotoUrlList().forEach(photoUrl -> {
            Photo photo = new Photo(post, photoUrl);
            postPort.savePhoto(photo);
        });
    }

    public void deletePhotos(PostPhotoDeleteCommand command) {
        List<String> imageUrls = command.getDeleteImageUrlList();
        postDeletePort.deleteImagesFromS3(imageUrls);
    }
}
