package com.spoony.spoony_server.adapter.out.persistence.feed;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockRepository;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.feed.mapper.FeedMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PostMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.db.NewFollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.NewFollowRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionEntity;

import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.annotation.Adapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Adapter
@Transactional
@RequiredArgsConstructor
public class FeedPersistenceAdapter implements FeedPort {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NewFollowRepository newFollowRepository;
    private final BlockRepository blockRepository;
    private final RegionRepository regionRepository;


    @Override
    public List<Feed> findFeedListByFollowing(Long userId) {
        return feedRepository.findByUser_UserId(userId)
                .stream()
                .map(FeedMapper::toDomain)
                .toList();
    }





    @Override
    public void deleteFeedByUserIdAndPostId(Long userId, Long postId) {
        feedRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public void addFeedsIfNotExists(User user, List<Post> posts) {
        Long userId = user.getUserId();
        List<Long> postIds = posts.stream()
                .map(Post::getPostId)
                .toList();

        //기존 피드 조회
        List<Long> existingPostIds = feedRepository.findPostIdsByUserIdAndPostIds(userId, postIds);
        List<FeedEntity> newFeeds = posts.stream()
                .filter(post -> !existingPostIds.contains(post.getPostId()))
                .map(post -> FeedEntity.builder()
                        .user(UserMapper.toEntity(user))
                        .post(PostMapper.toEntity(post))
                        .author(PostMapper.toEntity(post).getUser()) // 또는 Post에서 작성자 유저 꺼내기
                        .build())
                .toList();
        if (!newFeeds.isEmpty()) {
            feedRepository.saveAll(newFeeds);
        }
    }


    }

