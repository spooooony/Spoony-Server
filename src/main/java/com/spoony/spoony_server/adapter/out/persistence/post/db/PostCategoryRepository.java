package com.spoony.spoony_server.adapter.out.persistence.post.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategoryEntity, Long> {
    Optional<PostCategoryEntity> findByPost_PostId(Long postID);

    @Query("SELECT pc FROM PostCategoryEntity pc WHERE pc.post.postId IN :postIds")
    List<PostCategoryEntity> findPostCategoriesByPostIds(@Param("postIds") List<Long> postIds);
}

