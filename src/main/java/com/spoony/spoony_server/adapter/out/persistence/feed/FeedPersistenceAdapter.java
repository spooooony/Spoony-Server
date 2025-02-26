package com.spoony.spoony_server.adapter.out.persistence.feed;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.feed.mapper.FeedMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedPersistenceAdapter implements FeedPort {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void saveFollowerFeed(Long userId, Long postId) {

    }

    @Override
    public List<Feed> findFeedByUserId(Long userId) {
        return feedRepository.findByUser_UserId(userId)
                .stream()
                .map(FeedMapper::toDomain)
                .toList();
    }

    @Override
    public void saveFollowersFeed(List<Follow> followList, Post post) {
        List<FeedEntity> feedList = followList.stream()
                .map(follower -> {
                    UserEntity userEntity = userRepository.findById(follower.getFollower().getUserId())
                            .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
                    PostEntity postEntity = postRepository.findById(post.getPostId())
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
                    return FeedEntity.builder()
                            .user(userEntity)
                            .post(postEntity)
                            .build();
                })
                .toList();

        feedRepository.saveAll(feedList);
    }

    public void deleteFeedByUserIdAndPostId(Long userId, Long postId) {
        feedRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
    }
}
