package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;

public class PostMapper {

    public static Post toDomain(PostEntity postEntity) {
        return new Post(
                postEntity.getPostId(),
                new User(
                        postEntity.getUser().getUserId(),
                        postEntity.getUser().getProvider(),
                        postEntity.getUser().getProviderId(),
                        postEntity.getUser().getUserName(),
                        postEntity.getUser().getUserImage(),
                        RegionMapper.toDomain(postEntity.getUser().getRegion()),
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
                postEntity.getTitle(),
                postEntity.getDescription(),
                postEntity.getCreatedAt(),
                postEntity.getUpdatedAt()
        );
    }
}
