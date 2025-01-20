package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.FeedEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    List<FeedEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}




