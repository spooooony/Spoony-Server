package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Optional<MenuEntity> findMenuByPostId(Long postId);

    Long countByPostId(Long postId);

    List<PostEntity> findByUser_UserId(Long userId);

}
