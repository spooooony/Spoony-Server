package com.spoony.spoony_server.adapter.out.persistence.zzim.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.zzim.ZzimPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long>, JpaSpecificationExecutor<ZzimPostEntity> {
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

    @Modifying
    @Query(value = "INSERT IGNORE INTO zzim_post (user_id, post_id, author_id, created_at) VALUES (:userId, :postId, :authorId, NOW())", nativeQuery = true)
    int insertIfAbsent(@Param("userId") Long userId,
                       @Param("postId") Long postId,
                       @Param("authorId") Long authorId);

    @Modifying
    @Query(value = "DELETE FROM zzim_post WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    int deleteOne(@Param("userId") Long userId, @Param("postId") Long postId);

    //유저 신고에서 사용
    void deleteByUserAndAuthorAndPost(UserEntity user, UserEntity author, PostEntity post);
}


