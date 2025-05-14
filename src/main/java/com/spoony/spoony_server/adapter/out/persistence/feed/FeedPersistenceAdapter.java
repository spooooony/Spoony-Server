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
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionEntity;

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
    public void updateFeedFromNewFollowers(Long userId) {
        List<NewFollowEntity> newFollows = newFollowRepository.findByNewFollower_UserId(userId);

        for (NewFollowEntity newFollow : newFollows) {
            Long newFollowingUserId = newFollow.getNewFollowing().getUserId();

            // 새로 팔로우한 사용자의 모든 게시물 가져오기
            List<PostEntity> posts = postRepository.findByUser_UserId(newFollowingUserId);

            List<FeedEntity> feedEntities = posts.stream()
                    .map(post -> FeedEntity.builder()
                            .user(newFollow.getNewFollower())
                            .post(post)
                            .build())
                    .toList();

            feedRepository.saveAll(feedEntities);
        }

        // 처리 후 new_follower에서 삭제
        newFollowRepository.deleteAll(newFollows);
    }




    @Override
    public void deleteFeedByUserIdAndPostId(Long userId, Long postId) {
        feedRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
    }


}
