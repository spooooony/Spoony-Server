package com.spoony.spoony_server.adapter.out.persistence.post.jpa;

import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByUser(UserEntity userEntity);
}
