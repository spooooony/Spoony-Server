package com.spoony.spoony_server.application.service.feed;


import com.spoony.spoony_server.adapter.dto.post.*;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.CategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Category;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReportPort reportPort;


    @Transactional
    public FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command) {
        Long currentUserId = command.getUserId();

        //새 팔로우 관계에 대한 Feed 반영
        feedPort.updateFeedFromNewFollowers(currentUserId);

        // 1. 팔로우한 유저들의 게시물 리스트를 가져옴
        List<Feed> feedList = feedPort.findFeedListByFollowing(command.getUserId());

        // 2. 내가 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockedByMe = blockPort.getBlockedUserIds(command.getUserId());

        // 3. 나를 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockingMe = blockPort.getBlockerUserIds(command.getUserId());


        List<FeedResponseDTO> feedResponseList = feedList.stream()
                .filter(feed -> !userIdsBlockedByMe.contains(feed.getPost().getUser().getUserId())// 내가 차단한 유저의 게시물 제외
                        && !userIdsBlockingMe.contains(feed.getPost().getUser().getUserId())) // 나를 차단한 유저의 게시물 제외
                .map(feed -> {
                    Post post = feed.getPost();
                    User author = post.getUser();
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = postCategory.getCategory();

                    boolean isMine = currentUserId != null && currentUserId.equals(author.getUserId()); //작성자 식별
                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    String regionName = author.getRegion() != null ? author.getRegion().getRegionName() : null;

                    return new FeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            regionName,
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
                            post.getCreatedAt(),
                            isMine

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
        List<AgeGroup> ageGroups = command.getAgeGroups();
        Long cursor = command.getCursor();
        int size = command.getSize();
        Long currentUserId = command.getCurrentUserId();

        try {
            if (isLocalReviewFlag && categoryIds.size() == 1 && categoryIds.contains(2L)) {
                logger.info("✅✅✅ 로컬리뷰 전체 조회 (category 필터 제거)");
                filteredPosts = postPort.findFilteredPosts(
                        categoryIds,   // 카테고리 필터는 [2]로만 전달 (실제로는 [2]가 단독일 경우 category 필터를 제외할 것)
                        command.getRegionIds(),
                        ageGroups,
                        command.getSortBy(),
                        isLocalReviewFlag,
                        cursor,
                        size
                );
            } else {
                logger.info(isLocalReviewFlag ? "✅✅✅ 로컬리뷰: 필터링 로직 실행" : "일반리뷰: 필터링 로직 실행");
                filteredPosts = postPort.findFilteredPosts(
                        categoryIds,
                        command.getRegionIds(),
                        ageGroups,
                        command.getSortBy(),
                        isLocalReviewFlag,
                        cursor,
                        size
                );
            }
        } catch (Exception e) {
            logger.error("피드 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }

        logger.info("필터링된 게시물 수: {}", filteredPosts.size());

        // 차단 유저 필터링
        List<Long> userIdsBlockedByMe = blockPort.getBlockedUserIds(currentUserId);
        List<Long> userIdsBlockingMe = blockPort.getBlockerUserIds(currentUserId);

        //신고한 게시물 필터링
        List<Long> reportedPostIds = reportPort.findReportedPostIdsByUserId(currentUserId);

        filteredPosts = filteredPosts.stream()
                .filter(post -> {
                    Long authorId = post.getUser().getUserId();
                    Long postId = post.getPostId();
                    return !userIdsBlockedByMe.contains(authorId) && !userIdsBlockingMe.contains(authorId)&& !reportedPostIds.contains(postId);
                })
                .toList();

        logger.info("차단 유저 제외 후 게시물 수: {}", filteredPosts.size());

        List<FilteredFeedResponseDTO> feedResponseList = filteredPosts.stream()
                .map(post -> {
                    User author = post.getUser();
                    boolean isMine = currentUserId != null && currentUserId.equals(author.getUserId());


                    List<PostCategory> postCategories = postCategoryPort.findAllByPostId(post.getPostId());
                    Category mainCategory = postCategories.isEmpty() ? null : postCategories.get(0).getCategory();

                    String regionName = author.getRegion() != null ? author.getRegion().getRegionName() : null;

                    return new FilteredFeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            regionName,
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
                            isLocalReviewFlag,
                            isMine
                    );
                })
                .collect(Collectors.toList());

        // ✅ nextCursor 계산 (마지막 postId)
        Long nextCursor = filteredPosts.isEmpty() ? null :
                filteredPosts.get(filteredPosts.size() - 1).getPostId();

        logger.info("다음 커서(nextCursor): {}", nextCursor);
        return new FilteredFeedResponseListDTO(feedResponseList,nextCursor);
    }



}