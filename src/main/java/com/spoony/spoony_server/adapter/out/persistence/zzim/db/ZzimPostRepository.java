package com.spoony.spoony_server.adapter.out.persistence.zzim.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.zzim.ZzimPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {
    Long countByPost(PostEntity postEntity);
    Long countByPost_PostId(Long postId);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity); // user_id의 존재 여부 확인
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

    List<ZzimPostEntity> findByUser_UserId(Long userId);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}


