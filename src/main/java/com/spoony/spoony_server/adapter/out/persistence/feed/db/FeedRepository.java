package com.spoony.spoony_server.adapter.out.persistence.feed.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long>, JpaSpecificationExecutor<FeedEntity> {
    List<FeedEntity> findByUser_UserId(Long userId);
    void deleteByUser_UserIdAndPost_PostId(Long userId, Long postId);
    void deleteByUser_UserIdAndAuthor_UserId(Long userId, Long authorId);
}
