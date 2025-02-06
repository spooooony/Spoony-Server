package com.spoony.spoony_server.application.port.out.feed;

import com.spoony.spoony_server.adapter.out.persistence.feed.jpa.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;

import java.util.List;

public interface FeedPort {
    List<FeedEntity> findByUser(UserEntity userEntity);
    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
