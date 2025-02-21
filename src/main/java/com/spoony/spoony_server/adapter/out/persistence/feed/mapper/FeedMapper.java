package com.spoony.spoony_server.adapter.out.persistence.feed.mapper;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PostMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.feed.Feed;

public class FeedMapper {

    public static Feed toDomain(FeedEntity feedEntity) {
        return new Feed(
                feedEntity.getFeedId(),
                UserMapper.toDomain(feedEntity.getUser()),
                PostMapper.toDomain(feedEntity.getPost())
        );
    }
}
