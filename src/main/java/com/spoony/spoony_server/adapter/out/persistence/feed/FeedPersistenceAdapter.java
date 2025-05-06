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

//    @Override
//    public List<Post> findFilteredPosts(List<Long> categoryIds, List<Long> regionIds, boolean localReviewEnabled) {
//        Specification<PostEntity> spec = Specification.where(null);
//
//        List<String> regionNames = null;
//        if (regionIds != null && !regionIds.isEmpty()) {
//            regionNames = regionIds.stream()
//                    .map(id -> regionRepository.findById(id)
//                            .orElseThrow(() -> new RuntimeException("Region not found")))
//                    .map(RegionEntity::getRegionName)
//                    .toList();
//        }
//
//        if (localReviewEnabled) {
//            if (regionNames != null && !regionNames.isEmpty()) {
//                spec = spec.and(PostSpecification.withRegionIdsAndUserRegion(regionIds));
//                spec = spec.and(PostSpecification.withRegionNameInPlaceAddressByRegionNames(regionNames));
//            } else {
//                spec = spec.and(PostSpecification.withUserRegion());
//            }
//        } else {
//            if (regionNames != null && !regionNames.isEmpty()) {
//                spec = spec.and(PostSpecification.withRegionIds(regionIds));
//                spec = spec.and(PostSpecification.withRegionNameInPlaceAddressByRegionNames(regionNames));
//            }
//        }
//
//        if (categoryIds != null && !categoryIds.isEmpty()) {
//            spec = spec.and(PostSpecification.withCategoryIds(categoryIds));
//        }
//
//        List<PostEntity> filteredPostEntities = postRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return filteredPostEntities.stream()
//                .map(PostMapper::toDomain)
//                .collect(Collectors.toList());
//    }





//    @Override
//    public List<Feed> findFilteredFeeds(List<Long> categoryIds, List<Long> regionIds, boolean localReviewEnabled) {
//        Specification<FeedEntity> spec = Specification.where(null);
//
//        if (localReviewEnabled) {
//            if (regionIds != null && !regionIds.isEmpty()) {
//                // 작성자 지역 == place.region && 작성자 지역 ∈ regionIds
//                spec = spec.and(FeedSpecification.withRegionIdsAndUserRegion(regionIds));
//            } else {
//                // 작성자 지역 == place.region
//                spec = spec.and(FeedSpecification.withUserRegion());
//            }
//        } else {
//            if (regionIds != null && !regionIds.isEmpty()) {
//                // 작성자 지역 ∈ regionIds
//                spec = spec.and(FeedSpecification.withRegionIds(regionIds));
//            }
//        }
//
//        if (categoryIds != null && !categoryIds.isEmpty()) {
//            spec = spec.and(FeedSpecification.withCategoryIds(categoryIds));
//        }
//
//        return feedRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "post.createdAt"))
//                .stream()
//                .map(FeedMapper::toDomain)
//                .toList();
//    }

}
