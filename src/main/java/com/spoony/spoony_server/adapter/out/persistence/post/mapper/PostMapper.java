package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostMapper {

    public static Post toDomain(PostEntity postEntity) {
        Logger logger = LoggerFactory.getLogger(PostMapper.class);
        logger.info("PostEntity to Domain 변환 시작");
        return new Post(
                postEntity.getPostId(),
                new User(
                        postEntity.getUser().getUserId(),
                        postEntity.getUser().getPlatform(),

                        postEntity.getUser().getPlatformId(),
                        postEntity.getUser().getProfileImageLevel(),
                        postEntity.getUser().getLevel(),

                        postEntity.getUser().getUserName(),
                        RegionMapper.toDomain(postEntity.getUser().getRegion()),
                        postEntity.getUser().getIntroduction(),
                        postEntity.getUser().getBirth(),
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
}
