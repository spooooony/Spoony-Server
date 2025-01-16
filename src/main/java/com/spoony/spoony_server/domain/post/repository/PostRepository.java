package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    @Query("SELECT p.menu FROM PostEntity p WHERE p.postId = :postId")
    Optional<MenuEntity> findMenuByPostId(@Param("postId") Integer postId);
}
