package com.spoony.spoony_server.adapter.out.persistence.feed;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
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
