package com.spoony.spoony_server.application.service.feed;


import com.spoony.spoony_server.adapter.dto.post.*;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.CategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Category;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedService implements FeedGetUseCase {

    private final FeedPort feedPort;
    private  final PostPort postPort;
    private final PostCategoryPort postCategoryPort;
    private final ZzimPostPort zzimPostPort;
    private final BlockPort blockPort;
    private final CategoryPort categoryPort;


    @Transactional
    public FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command) {
        Long currentUserId = command.getUserId();

        //새 팔로우 관계에 대한 Feed 반영
        feedPort.updateFeedFromNewFollowers(currentUserId);

        // 1. 팔로우한 유저들의 게시물 리스트를 가져옴
        List<Feed> feedList = feedPort.findFeedListByFollowing(command.getUserId());

        // 2. 내가 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockedByMe = blockPort.findBlockedUserIds(command.getUserId());

        // 3. 나를 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockingMe = blockPort.findBlockingUserIds(command.getUserId());


        List<FeedResponseDTO> feedResponseList = feedList.stream()
                .filter(feed -> !userIdsBlockedByMe.contains(feed.getPost().getUser().getUserId())// 내가 차단한 유저의 게시물 제외
                        && !userIdsBlockingMe.contains(feed.getPost().getUser().getUserId())) // 나를 차단한 유저의 게시물 제외
                .map(feed -> {
                    Post post = feed.getPost();
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
                            new CategoryColorResponseDTO(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ),
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt()
                    );
                }).sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt())).toList();
        return new FeedListResponseDTO(feedResponseList);
    }



    @Transactional
    public FilteredFeedResponseListDTO getFilteredFeed(FeedFilterCommand command) {
        // 필터링된 게시물을 가져오는 과정에서 예외 처리
        List<Post> filteredPosts;
        try {
            filteredPosts = postPort.findFilteredPosts(
                    command.getCategoryIds(),
                    command.getRegionIds()
            );
        } catch (Exception e) {
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }

        List<FilteredFeedResponseDTO> feedResponseList = filteredPosts.stream()
                .map(post -> {
                    User author = post.getUser();

                    // PostCategory가 null일 경우 처리
                    PostCategory postCategory;
                    try {
                        postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    } catch (BusinessException e) {
                        // Category가 없는 경우, 로컬리뷰 여부를 false로 설정
                        postCategory = null;
                    }
                    Category category = (postCategory != null) ? postCategory.getCategory() : null;

                    // 사진 리스트가 null인 경우 처리
                    List<String> photoUrlList = new ArrayList<>();
                    if (category != null) {
                        List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                        if (photoList != null && !photoList.isEmpty()) {
                            photoUrlList = photoList.stream()
                                    .map(Photo::getPhotoUrl)
                                    .collect(Collectors.toList());
                        }
                    }

                    // category_id가 2일 경우 로컬리뷰로 간주
                    boolean isLocalReview = category != null && category.getCategoryId() == 2;

                    // 필터링된 게시물 DTO 생성
                    return new FilteredFeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            author.getRegion() != null ? author.getRegion().getRegionName() : "Unknown",  // 지역 이름
                            post.getPostId(),
                            post.getDescription(),
                            category != null ? new CategoryColorResponseDTO(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ) : null,
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt(),
                            isLocalReview  // 로컬리뷰인지 여부 추가
                    );
                })
                .collect(Collectors.toList());

        return new FilteredFeedResponseListDTO(feedResponseList);
    }





//    @Override
//    public FeedListResponseDTO getFilteredFeed(FeedFilterCommand command) {
//        // 1. 필터링된 Feed 가져오기
//        List<Feed> filteredFeeds = feedPort.findFilteredFeeds(
//                command.getCategoryIds(),
//                command.getRegionIds(),
//                command.isLocalReviewEnabled()
//        );
//
//        // 2. DTO 변환
//        List<FeedResponseDTO> feedResponseList = filteredFeeds.stream()
//                .map(feed -> {
//                    Post post = feed.getPost();
//                    User author = post.getUser();
//                    List<PostCategory> postCategories = postCategoryPort.findPostCategoriesByPostId(post.getPostId());
//                    // 카테고리 ID가 제공되지 않거나 일치하는 카테고리가 없다면 category_id가 1인 기본 카테고리 추가
//                    if (postCategories.isEmpty()) {
//                        Category defaultCategory = categoryPort.findCategoryById(1L); // category_id = 1 (전체조회) 카테고리
//                        PostCategory defaultPostCategory = new PostCategory(post, defaultCategory);
//                        postCategories.add(defaultPostCategory);
//                    }
//                    // 여러 카테고리 가능 - 중복 가능
//                    List<Category> categories = postCategories.stream()
//                            .map(PostCategory::getCategory)
//                            .collect(Collectors.toList());
//                    Category category = categories.get(0);  // 첫 번째 카테고리 (선택된 카테고리)
//                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
//                    List<String> photoUrlList = photoList.stream()
//                            .map(Photo::getPhotoUrl)
//                            .toList();
//
//                    return new FeedResponseDTO(
//                            author.getUserId(),
//                            author.getUserName(),
//                            author.getRegion().getRegionName(),
//                            post.getPostId(),
//                            post.getDescription(),
//                            new CategoryColorResponseDTO(
//                                    category.getCategoryId(),
//                                    category.getCategoryName(),
//                                    category.getIconUrlColor(),
//                                    category.getTextColor(),
//                                    category.getBackgroundColor()
//                            ),
//                            zzimPostPort.countZzimByPostId(post.getPostId()),
//                            photoUrlList,
//                            post.getCreatedAt()
//                    );
//                })
//                .sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt())) // 최신순 정렬
//                .toList();
//
//        return new FeedListResponseDTO(feedResponseList);
//    }

    private List<FeedResponseDTO> buildFeedResponseDTOList(List<Post> postList) {
        return postList.stream()
                .map(this::toFeedResponseDTO)
                .sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()))
                .toList();
    }

    private FeedResponseDTO toFeedResponseDTO(Post post) {
        User author = post.getUser();

        List<PostCategory> postCategories = postCategoryPort.findPostCategoriesByPostId(post.getPostId());
        if (postCategories.isEmpty()) {
            Category defaultCategory = categoryPort.findCategoryById(1L);
            postCategories.add(new PostCategory(post, defaultCategory));
        }

        Category category = postCategories.get(0).getCategory();

        List<String> photoUrlList = postPort.findPhotoById(post.getPostId()).stream()
                .map(Photo::getPhotoUrl)
                .toList();

        return new FeedResponseDTO(
                author.getUserId(),
                author.getUserName(),
                author.getRegion().getRegionName(),
                post.getPostId(),
                post.getDescription(),
                new CategoryColorResponseDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getIconUrlColor(),
                        category.getTextColor(),
                        category.getBackgroundColor()
                ),
                zzimPostPort.countZzimByPostId(post.getPostId()),
                photoUrlList,
                post.getCreatedAt()
        );
    }


}