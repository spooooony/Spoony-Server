package com.spoony.spoony_server.adapter.out.persistence.feed.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    List<FeedEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
    void deleteByUser_UserIdAndPost_PostId(Long userId, Long postId);
}
