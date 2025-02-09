package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.domain.user.Follow;

public class FollowMapper {

    public static Follow toDomain(FollowEntity followEntity) {

        return new Follow(
                followEntity.getFollowId(),
                UserMapper.toDomain(followEntity.getFollower()),
                UserMapper.toDomain(followEntity.getFollowing())
        );
    }
}
