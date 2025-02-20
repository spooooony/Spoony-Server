package com.spoony.spoony_server.application.port.out.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.domain.zzim.ZzimPost;

import java.util.List;

public interface ZzimPostPort {
    Long countZzimByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Photo findFistPhotoById(Long postId);
    List<Photo> findPhotoListById(Long postId);
    List<ZzimPost> findUserByUserId(Long userId);
    void saveZzimPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);

}
