package com.spoony.spoony_server.adapter.out.persistence.post.db;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> , JpaSpecificationExecutor<PostEntity> {

    List<PostEntity> findByUser_UserId(Long userId);
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.user.userId = :userId AND (:reportedPostIds IS NULL OR p.postId NOT IN :reportedPostIds)")
    Long countByUser_UserId(@Param("userId") Long userId,
                            @Param("reportedPostIds") List<Long> reportedPostIds);

    @Query(value = "SELECT 1 FROM post WHERE post_id = :postId FOR UPDATE", nativeQuery = true)
    void lockPostForUpdate(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update PostEntity p set p.zzimCount = coalesce(p.zzimCount,0) + 1 where p.postId = :postId")
    void incrementZzimCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update PostEntity p set p.zzimCount = case when coalesce(p.zzimCount,0)>0 then p.zzimCount-1 else 0 end where p.postId = :postId")
    void decrementZzimCount(@Param("postId") Long postId);

    //Long countByUser_UserId(Long userId);
    List<PostEntity> findByDescriptionContaining(String query);

    @Query("SELECT r.post.postId  FROM ReportEntity r WHERE r.user.userId = :userId")
    List<Long> findReportedPostIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM PostEntity p JOIN ReportEntity r ON p.postId = r.post.postId")
    List<PostEntity> findReportedPosts(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT p) FROM PostEntity p JOIN ReportEntity r ON p.postId = r.post.postId")
    int countReportedPosts();

    @Query("SELECT p FROM PostEntity p WHERE p.user.userId = :userId ORDER BY p.createdAt DESC")
    List<PostEntity> findByUserIdWithPaging(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.user.userId = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM post WHERE post_id = :postId", nativeQuery = true)
    Optional<PostEntity> findByIdIncludingDeleted(@Param("postId") Long postId);

    @Query(value = "SELECT * FROM post WHERE is_deleted = true ORDER BY created_at DESC", nativeQuery = true)
    List<PostEntity> findDeletedPosts(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM post WHERE is_deleted = true", nativeQuery = true)
    long countDeletedPosts();
}
