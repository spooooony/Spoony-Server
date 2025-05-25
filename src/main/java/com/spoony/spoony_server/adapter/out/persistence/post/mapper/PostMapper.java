package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.place.mapper.PlaceMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;

public class PostMapper {

    public static Post toDomain(PostEntity postEntity) {
        return new Post(
                postEntity.getPostId(),
                new User(
                        postEntity.getUser().getUserId(),
                        postEntity.getUser().getPlatform(),
                        postEntity.getUser().getPlatformId(),
                        postEntity.getUser().getProfileImageLevel(),
                        postEntity.getUser().getLevel(),
                        postEntity.getUser().getUserName(),
                        postEntity.getUser().getRegion() != null
                                ? RegionMapper.toDomain(postEntity.getUser().getRegion())
                                : null,
                        postEntity.getUser().getIntroduction(),
                        postEntity.getUser().getBirth(),
                        postEntity.getUser().getAgeGroup(),
                        postEntity.getUser().getCreatedAt(),
                        postEntity.getUser().getUpdatedAt()
                ),
                new Place(
                        postEntity.getPlace().getPlaceId(),
                        postEntity.getPlace().getPlaceName(),
                        postEntity.getPlace().getPlaceAddress(),
                        postEntity.getPlace().getPlaceRoadAddress(),
                        postEntity.getPlace().getLatitude(),
                        postEntity.getPlace().getLongitude()
                ),
                postEntity.getDescription(),
                postEntity.getValue(),
                postEntity.getCons(),
                postEntity.getCreatedAt(),
                postEntity.getUpdatedAt()
        );
    }
    public static PostEntity toEntity(Post post) {
        if (post == null) return null;

        return PostEntity.builder()
                .postId(post.getPostId())
                .user(UserMapper.toEntity(post.getUser()))
                .place(PlaceMapper.toEntity(post.getPlace()))
                .description(post.getDescription())
                .value(post.getValue())
                .cons(post.getCons())
                .build();
    }
}
