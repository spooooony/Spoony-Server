package com.spoony.spoony_server.application.service.post;

import com.spoony.spoony_server.adapter.dto.post.*;
import com.spoony.spoony_server.adapter.dto.user.UserSearchResultDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSearchResultListDTO;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import com.spoony.spoony_server.application.port.command.post.*;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserSearchCommand;
import com.spoony.spoony_server.application.port.in.post.*;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.application.port.out.post.*;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

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
    private final FeedPort feedPort;
    private final PostDeletePort postDeletePort;
    private final PhotoPort photoPort;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PostResponseDTO getPostById(PostGetCommand command) {

        Post post = postPort.findPostById(command.getPostId());
        User user = userPort.findUserById(command.getUserId());

        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
        Long categoryId = postCategory.getCategory().getCategoryId();
        Category category = categoryPort.findCategoryById(categoryId);

        Place place = post.getPlace();
        LocalDateTime latestDate = post.getUpdatedAt().isAfter(post.getCreatedAt()) ? post.getUpdatedAt() : post.getCreatedAt();

        Long zzimCount = zzimPostPort.countZzimByPostId(post.getPostId());
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

        return new PostResponseDTO(command.getPostId(),
                post.getUser().getUserId(),
                photoUrlList,
                latestDate,
                menuNameList,
                post.getDescription(),
                place.getPlaceName(),
                place.getPlaceAddress(),
                place.getLatitude(),
                place.getLongitude(),
                zzimCount,
                isMine,
                isZzim,
                isScoop,
                new CategoryColorResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlColor(),
                        category.getTextColor(),
                        category.getBackgroundColor())
        );
    }
    public FeedListResponseDTO getPostsByUserId(UserGetCommand command){
        Long userId = command.getUserId();

        //1. 유저가 작성한 게시물 모두 조회
        List<Post> postList = postPort.findPostsByUserId(userId)
;
        //2. 각 Post -> Feed
        List<FeedResponseDTO> feedResponseList = postList.stream().map(post -> {
                    User author = post.getUser();
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = postCategory.getCategory();
                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();
                    return new FeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            author.getRegion().getRegionName(),
                            post.getPostId(),
                            post.getDescription(),
                            new CategoryColorResponseDTO(category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()

                    ),
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
        return new FeedListResponseDTO(feedResponseList);
    }

    @Override
    public ReviewAmountResponseDTO getPostAmountByUserId(UserGetCommand command) {
        Long userId = command.getUserId();
        Long reviewCount = postPort.countPostsByUserId(userId);
        return new ReviewAmountResponseDTO(reviewCount);

    }
    @Override
    public PostSearchResultListDTO searchReviewsByQuery(PostSearchCommand command){
        List<Post> postList = postPort.findByPostDescriptionContaining(command.getQuery());

        List<FeedResponseDTO> postSearchResultList = postList.stream()
                .map(post -> {
                        List<String> phothUrlList = postPort.findPhotoById(post.getPostId()).stream()
                                .map(Photo::getPhotoUrl).toList();
                        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                        Category category = categoryPort.findCategoryById(postCategory.getCategory().getCategoryId());
                    Long zzimCount = zzimPostPort.countZzimByPostId(post.getPostId());
                    String userRegion = post.getUser().getRegion().getRegionName();

                    LocalDateTime latestDate = post.getUpdatedAt().isAfter(post.getCreatedAt())
                            ? post.getUpdatedAt() : post.getCreatedAt();

                    return new FeedResponseDTO(
                            post.getUser().getUserId(),
                            post.getUser().getUserName(),
                            userRegion,
                            post.getPostId(),
                            post.getDescription(),
                            new CategoryColorResponseDTO(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ),
                            zzimCount,
                            phothUrlList,
                            latestDate
                    );
                })
                .toList();

        return new PostSearchResultListDTO(postSearchResultList);
    }


    @Override
    public PostSearchHistoryResponseDTO getReviewSearchHistory(UserGetCommand command) {
        return null;
    }

    public List<String> savePostImages(PostPhotoSaveCommand photoSaveCommand) throws IOException {
        List<String> photoUrlList = postCreatePort.savePostImages(photoSaveCommand.getPhotos());
        return photoUrlList;
    }

    @Transactional
    public void createPost(PostCreateCommand command) {
        // 게시글 업로드
        User user = userPort.findUserById(command.getUserId());
        Category category = categoryPort.findCategoryById(command.getCategoryId());

        Place place = new Place(
                command.getPlaceName(),
                command.getPlaceAddress(),
                command.getPlaceRoadAddress(),
                command.getLatitude(),
                command.getLongitude()
        );

        Long placeId = placePort.savePlace(place);
        place = placePort.findPlaceById(placeId);

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
        Activity activity = spoonPort.findActivityByActivityId(2L);
        spoonPort.updateSpoonBalanceByActivity(user, activity);

        // 스푼 히스토리 기록
        spoonPort.updateSpoonHistoryByActivity(user, activity);

        // 작성자 지도 리스트에 게시물 추가
        zzimPostPort.saveZzimPost(user, post);

        // 작성자를 팔로우하는 사용자들의 피드에 게시물 추가
        List<Follow> followList = userPort.findFollowersByUserId(user.getUserId());
        List<Long> followerIds = followList.stream().map(follow -> follow.getFollower().getUserId()).toList();

        // Event 발행
        eventPublisher.publishEvent(new PostCreatedEvent(this, followerIds, post.getPostId()));
    }

    // 모든 카테고리 조회
    @Transactional
    public CategoryMonoListResponseDTO getAllCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryPort.findAllCategories().stream()
                .map(category -> new CategoryMonoResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .toList();

        return new CategoryMonoListResponseDTO(categoryMonoResponseDTOList);
    }

    // 음식 카테고리 조회
    @Transactional
    public CategoryMonoListResponseDTO getFoodCategories() {
        List<CategoryMonoResponseDTO> categoryMonoResponseDTOList = categoryPort.findFoodCategories().stream()
                .map(category -> new CategoryMonoResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlBlack(),
                        category.getIconUrlWhite()))
                .toList();

        return new CategoryMonoListResponseDTO(categoryMonoResponseDTOList);
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

        // 사용자의 피드에서 게시물 삭제
        feedPort.deleteFeedByUserIdAndPostId(command.getUserId(), command.getPostId());
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
        List<String> imageUrls = photoPort.getPhotoUrls(command.getPostId());
        postDeletePort.deleteImagesFromS3(imageUrls);

        postPort.updatePost(command.getPostId(), command.getDescription(), command.getValue(), command.getCons());
        Post post = postPort.findPostById(command.getPostId());

        postPort.deleteAllPostCategoryByPostId(command.getPostId());
        postPort.deleteAllMenusByPostId(command.getPostId());
        postPort.deleteAllPhotosByPostId(command.getPostId());

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

    public void deletePhotos(PostDeleteCommand command) {
        List<String> imageUrls = photoPort.getPhotoUrls(command.getPostId());
        postDeletePort.deleteImagesFromS3(imageUrls);
    }


}
