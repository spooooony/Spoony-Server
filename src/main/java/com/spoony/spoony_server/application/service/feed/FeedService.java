package com.spoony.spoony_server.application.service.feed;

import com.spoony.spoony_server.adapter.dto.post.response.*;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
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
    private final ReportPort reportPort;

    @Transactional
    public FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command) {
        Long currentUserId = command.getUserId();

        // 1. 팔로우한 유저들의 게시물 리스트를 가져옴(이 경우, 언팔로우 상태가 반영x)
        List<Feed> feedList = feedPort.findFeedListByFollowing(command.getUserId());

        // 2. 내가 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockedByMe = blockPort.getBlockedUserIds(command.getUserId());

        // 3. 나를 차단한 유저들의 ID를 가져옴
        List<Long> userIdsBlockingMe = blockPort.getBlockerUserIds(command.getUserId());

        //4. feed테이블엔 남아있지만, 언팔로우 상태 반영해서 필터링
        List<Long> unfollowedUserIds = blockPort.getUnfollowedUserIds(currentUserId);


        List<FeedResponseDTO> feedResponseList = feedList.stream()
                .filter(feed -> !userIdsBlockedByMe.contains(feed.getPost().getUser().getUserId())// 내가 차단한 유저의 게시물 제외
                        && !userIdsBlockingMe.contains(feed.getPost().getUser().getUserId())// 나를 차단한 유저의 게시물 제외
                        && !unfollowedUserIds.contains(feed.getPost().getUser().getUserId())
                )
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

                    return FeedResponseDTO.of(
                            author.getUserId(),
                            author.getUserName(),
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
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt(),
                            isMine
                    );
                }).sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt())).toList();
        return FeedListResponseDTO.of(feedResponseList);
    }

    @Transactional
    public FilteredFeedResponseListDTO getFilteredFeed(FeedFilterCommand command) {

        List<Post> filteredPosts;
        List<Long> categoryIds = command.getCategoryIds();
        boolean isLocalReviewFlag = command.isLocalReview();
        List<AgeGroup> ageGroups = command.getAgeGroups();
        Long cursor = command.getCursor();
        int size = command.getSize();
        Long currentUserId = command.getCurrentUserId();

        try {
            List<Long> blockedUserIds = blockPort.getBlockedUserIds(currentUserId);
            List<Long> reportedUserIds = blockPort.getBlockerUserIds(currentUserId);
            List<Long> reportedPostIds = postPort.getReportedPostIds(currentUserId);

            filteredPosts = postPort.findFilteredPosts(
                    categoryIds,
                    command.getRegionIds(),
                    ageGroups,
                    command.getSortBy(),
                    isLocalReviewFlag,
                    cursor,
                    size,
                    blockedUserIds,
                    reportedUserIds,
                    reportedPostIds
            );
        } catch (Exception e) {
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }


        // 🔹 응답용 DTO 변환
        List<FilteredFeedResponseDTO> feedResponseList = filteredPosts.stream()
                .map(post -> {
                    User author = post.getUser();
                    boolean isMine = currentUserId != null && currentUserId.equals(author.getUserId());

                    List<PostCategory> postCategories = postCategoryPort.findAllByPostId(post.getPostId());
                    Category mainCategory = postCategories.isEmpty() ? null : postCategories.get(0).getCategory();

                    String regionName = author.getRegion() != null ? author.getRegion().getRegionName() : null;

                    return FilteredFeedResponseDTO.of(
                            author.getUserId(),
                            author.getUserName(),
                            regionName,
                            post.getPostId(),
                            post.getDescription(),
                            mainCategory != null ?
                                    CategoryColorResponseDTO.of(
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

        Long nextCursor = filteredPosts.isEmpty() ? null :
                filteredPosts.get(filteredPosts.size() - 1).getPostId();

        return FilteredFeedResponseListDTO.of(feedResponseList, nextCursor);

    }
}
