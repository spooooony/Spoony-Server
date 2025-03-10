package com.spoony.spoony_server.application.port.out.zzim;

import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.domain.zzim.ZzimPost;

import java.util.List;
import java.util.Map;

public interface ZzimPostPort {
    Long countZzimByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Photo findFistPhotoById(Long postId);
    List<Photo> findPhotoListById(Long postId);
    List<ZzimPost> findUserByUserId(Long userId);
    void saveZzimPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    Map<Long, Photo> findFirstPhotosByPostIds(List<Long> postIds); // 🔥 추가된 메서드
}
