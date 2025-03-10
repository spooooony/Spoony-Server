package com.spoony.spoony_server.adapter.out.persistence.post.db;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findByUser_UserId(Long userId);

    @EntityGraph(attributePaths = {"photos", "postCategories", "postCategories.category"})
    @Query("SELECT p FROM PostEntity p WHERE p.postId = :postId")
    Optional<PostEntity> findPostWithPhotosAndCategories(@Param("postId") Long postId);

}
