package com.spoony.spoony_server.application.port.out.feed;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Follow;

import java.util.List;

public interface FeedPort {
    List<FeedEntity> findByUser(UserEntity userEntity);
    void saveFollowersFeed(List<Follow> followList, Post post);
    void deleteFeedByUserIdAndPostId(Long userId, Long postId);
}
