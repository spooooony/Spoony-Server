package com.spoony.spoony_server.application.port.out.feed;

import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Follow;

import java.util.List;

public interface FeedPort {
    List<Feed> findFeedListByFollowing(Long userId);
    void updateFeedFromNewFollowers(Long userId);

    //List<Feed> findFilteredFeeds(List<Long> categoryIds, List<Long> regionIds, boolean localReviewEnabled);

    void deleteFeedByUserIdAndPostId(Long userId, Long postId);

    //List<Post> findFilteredPosts(List<Long> categoryIds, List<Long> regionIds, boolean localReviewEnabled);
}
