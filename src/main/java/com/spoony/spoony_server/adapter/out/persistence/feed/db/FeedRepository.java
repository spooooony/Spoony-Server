package com.spoony.spoony_server.adapter.out.persistence.feed.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    List<FeedEntity> findByUser_UserId(Long userId);
    void deleteByUser_UserIdAndPost_PostId(Long userId, Long postId);
}
