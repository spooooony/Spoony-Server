package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> , JpaSpecificationExecutor<PostEntity> {

    List<PostEntity> findByUser_UserId(Long userId);
    Long countByUser_UserId(Long userId);
    List<PostEntity> findByDescriptionContaining(String query);

}
