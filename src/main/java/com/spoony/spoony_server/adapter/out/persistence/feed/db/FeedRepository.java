package com.spoony.spoony_server.adapter.out.persistence.feed.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long>, JpaSpecificationExecutor<FeedEntity> {
    List<FeedEntity> findByUser_UserId(Long userId);
    void deleteByUser_UserIdAndPost_PostId(Long userId, Long postId);
    void deleteByUser_UserIdAndAuthor_UserId(Long userId, Long authorId);
    @Query("SELECT f.post.postId FROM FeedEntity f WHERE f.user.userId = :userId AND f.post.postId IN :postIds")
    List<Long> findPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

}
