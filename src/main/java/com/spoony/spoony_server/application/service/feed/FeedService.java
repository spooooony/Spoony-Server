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
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
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
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("getFilteredFeed 호출됨");
        logger.info("FeedFilterCommand: {}", command);

        List<Post> filteredPosts;
        List<Long> categoryIds = command.getCategoryIds();
        boolean isLocalReviewFlag = command.isLocalReview();

        try {
            if (isLocalReviewFlag && categoryIds.size() == 1 && categoryIds.contains(2L)) {
                logger.info("✅✅✅ 로컬리뷰 전체 조회 (category 필터 제거)");
                filteredPosts = postPort.findFilteredPosts(
                        categoryIds,   // 카테고리 필터는 [2]로만 전달 (실제로는 [2]가 단독일 경우 category 필터를 제외할 것)
                        command.getRegionIds(),
                        command.getSortBy(),
                        isLocalReviewFlag // 로컬리뷰 플래그 그대로 전달
                );
            } else {
                logger.info(isLocalReviewFlag ? "✅✅✅ 로컬리뷰: 필터링 로직 실행" : "일반리뷰: 필터링 로직 실행");
                filteredPosts = postPort.findFilteredPosts(
                        categoryIds,
                        command.getRegionIds(),
                        command.getSortBy(),
                        isLocalReviewFlag
                );
            }
        } catch (Exception e) {
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }

        logger.info("필터링된 게시물 수: {}", filteredPosts.size());

        List<FilteredFeedResponseDTO> feedResponseList = filteredPosts.stream()
                .map(post -> {
                    User author = post.getUser();
                    List<PostCategory> postCategories = postCategoryPort.findAllByPostId(post.getPostId());
                    Category mainCategory = postCategories.isEmpty() ? null : postCategories.get(0).getCategory();

                    return new FilteredFeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            author.getRegion() != null ? author.getRegion().getRegionName() : "Unknown",
                            post.getPostId(),
                            post.getDescription(),
                            mainCategory != null ?
                                    new CategoryColorResponseDTO(
                                            mainCategory.getCategoryId(),
                                            mainCategory.getCategoryName(),
                                            mainCategory.getIconUrlColor(),
                                            mainCategory.getTextColor(),
                                            mainCategory.getBackgroundColor()
                                    ) : null,
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            postPort.findPhotoById(post.getPostId()).stream()
                                    .map(Photo::getPhotoUrl)
                                    .collect(Collectors.toList()),
                            post.getCreatedAt(),
                            isLocalReviewFlag  // 여기서 command의 플래그 그대로 사용
                    );
                })
                .collect(Collectors.toList());

        return new FilteredFeedResponseListDTO(feedResponseList);
    }


//    @Transactional
//    public FilteredFeedResponseListDTO getFilteredFeed(FeedFilterCommand command) {
//        // 필터링된 게시물을 가져오는 과정에서 예외 처리
//        List<Post> filteredPosts;
//        try {
//            // 새로운 방식으로 필터링된 게시물 목록 가져오기
//            filteredPosts = postPort.findFilteredPosts(
//                    command.getCategoryIds(),
//                    command.getRegionIds(),
//                    command.getSortBy()
//            );
//        } catch (Exception e) {
//            // 예외 발생 시 처리
//            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
//        }
//
//        // 필터링된 게시물을 DTO로 변환
//        List<FilteredFeedResponseDTO> feedResponseList = filteredPosts.stream()
//                .map(post -> {
//                    User author = post.getUser();
//
//                    // PostCategory가 null일 경우 처리
//                    PostCategory postCategory;
//                    try {
//                        postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
//                    } catch (BusinessException e) {
//                        // Category가 없는 경우, 로컬리뷰 여부를 false로 설정
//                        postCategory = null;
//                    }
//                    Category category = (postCategory != null) ? postCategory.getCategory() : null;
//
//                    // 사진 리스트가 null인 경우 처리
//                    List<String> photoUrlList = new ArrayList<>();
//                    if (category != null) {
//                        List<Photo> photoList = postPort.findPhotoById(post.getPostId());
//                        if (photoList != null && !photoList.isEmpty()) {
//                            photoUrlList = photoList.stream()
//                                    .map(Photo::getPhotoUrl)
//                                    .collect(Collectors.toList());
//                        }
//                    }
//
//                    // category_id가 2일 경우 로컬리뷰로 간주
//                    boolean isLocalReview = category != null && category.getCategoryId() == 2;
//
//                    // 필터링된 게시물 DTO 생성
//                    return new FilteredFeedResponseDTO(
//                            author.getUserId(),
//                            author.getUserName(),
//                            author.getRegion() != null ? author.getRegion().getRegionName() : "Unknown",  // 지역 이름
//                            post.getPostId(),
//                            post.getDescription(),
//                            category != null ? new CategoryColorResponseDTO(
//                                    category.getCategoryId(),
//                                    category.getCategoryName(),
//                                    category.getIconUrlColor(),
//                                    category.getTextColor(),
//                                    category.getBackgroundColor()
//                            ) : null,
//                            zzimPostPort.countZzimByPostId(post.getPostId()),
//                            photoUrlList,
//                            post.getCreatedAt(),
//                            isLocalReview  // 로컬리뷰인지 여부 추가
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // DTO 리스트를 포함한 응답 반환
//        return new FilteredFeedResponseListDTO(feedResponseList);
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