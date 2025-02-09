package com.spoony.spoony_server.application.port.out.zzim;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface ZzimPort {
    Long countZzimByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    List<ZzimPostEntity> findByUser(UserEntity userEntity);
    void saveZzimPost(User user, Post post);
}
