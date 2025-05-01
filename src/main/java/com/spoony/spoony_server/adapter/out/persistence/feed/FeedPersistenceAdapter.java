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
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Adapter
@Transactional
@RequiredArgsConstructor
public class FeedPersistenceAdapter implements FeedPort {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void saveFollowerFeed(Long userId, Long postId) {

    }

    @Override
    public List<Feed> findFeedListByFollowing(Long userId) {
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

    @Override
    public void deleteFeedByUserIdAndPostId(Long userId, Long postId) {
        feedRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public List<Feed> searchFeedByFollowingWithFilters(Long userId, List<String> categories, String locationQuery){
        Specification<FeedEntity> spec = FeedSpecification.buildFeedSpec(userId, categories, locationQuery);
        return feedRepository.findAll(spec)
                .stream()
                .map(FeedMapper::toDomain)
                .toList();
    }

    @Override
    public List<Feed> searchAllFeedsWithFilters(List<String> categories, String locationQuery) {
        Specification<FeedEntity> spec = Specification.where(null); // 전체에서 조회이므로 userId 조건 x

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(FeedSpecification.withCategories(categories));
        }

        if (locationQuery != null && !locationQuery.isBlank()) {
            spec = spec.and(FeedSpecification.hasLocation(locationQuery));
        }

        return feedRepository.findAll(spec)
                .stream()
                .map(FeedMapper::toDomain)
                .toList();
    }

}
