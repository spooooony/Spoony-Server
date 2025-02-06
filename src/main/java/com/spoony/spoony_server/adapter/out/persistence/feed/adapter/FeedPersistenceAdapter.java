package com.spoony.spoony_server.adapter.out.persistence.feed.adapter;

import com.spoony.spoony_server.adapter.out.persistence.feed.jpa.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.jpa.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedPersistenceAdapter implements FeedPort {

    private final FeedRepository feedRepository;

    @Override
    public void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity) {
        feedRepository.deleteByUserAndPost(userEntity, postEntity);
    }

    @Override
    public List<FeedEntity> findByUser(UserEntity userEntity) {
        return feedRepository.findByUser(userEntity);
    }
}
