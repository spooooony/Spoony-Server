package com.spoony.spoony_server.application.port.out.feed;

import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface FeedPort {
    List<Feed> findFeedListByFollowing(Long userId);
    void deleteFeedByUserIdAndPostId(Long userId, Long postId);
    void addFeedsIfNotExists(User user, List<Post> posts);
    void deleteByUserIdAndAuthorId(Long userId, Long authorId);

    void deleteOneWay(Long userId, Long authorId);       // 언팔로우
    void deleteBidirectional(Long userId, Long authorId); // 차단, 신고
}


