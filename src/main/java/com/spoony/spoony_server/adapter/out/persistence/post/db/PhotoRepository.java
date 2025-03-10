package com.spoony.spoony_server.adapter.out.persistence.post.db;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {
    Optional<List<PhotoEntity>> findByPost_PostId(Long postId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.post.postId IN :postIds GROUP BY p.post.postId")
    List<PhotoEntity> findFirstPhotosByPostIds(@Param("postIds") List<Long> postIds);

}
