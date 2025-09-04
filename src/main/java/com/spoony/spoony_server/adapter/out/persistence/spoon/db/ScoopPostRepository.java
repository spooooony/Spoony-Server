package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.ScoopPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScoopPostRepository extends JpaRepository<ScoopPostEntity, Long> {
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT IGNORE INTO scoop_post (user_id, post_id) VALUES (:uid, :pid)", nativeQuery = true)
    int insertIfAbsent(@Param("uid") Long userId, @Param("pid") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ScoopPostEntity sp WHERE sp.user.userId = :uid AND sp.post.postId = :pid")
    void deleteOne(@Param("uid") Long userId, @Param("pid") Long postId);
}


