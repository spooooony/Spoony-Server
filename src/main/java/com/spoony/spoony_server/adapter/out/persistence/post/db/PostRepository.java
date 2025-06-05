package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> , JpaSpecificationExecutor<PostEntity> {

    List<PostEntity> findByUser_UserId(Long userId);
    Long countByUser_UserId(Long userId);
    List<PostEntity> findByDescriptionContaining(String query);

    @Query("SELECT r.post.postId  FROM ReportEntity r WHERE r.user.userId = :userId")
    List<Long> findReportedPostIdsByUserId(@Param("userId") Long userId);

}
