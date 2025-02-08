package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.dto.post.CategoryType;

import java.util.List;
import java.util.Optional;

public interface PostPort {
    Optional<CategoryEntity> findByCategoryId(Long postId);
    List<CategoryEntity> findByCategoryType(CategoryType categoryType);
    List<FollowEntity> findByFollowing(UserEntity following);
    Optional<List<MenuEntity>> findMenuByPost(PostEntity postEntity);
    Optional<List<PhotoEntity>> findPhotoByPost(PostEntity post);
    Optional<PhotoEntity> findFirstByPost(PostEntity postEntity);
    Optional<PostCategoryEntity> findByPost(PostEntity post);
    List<PostEntity> findByUser(UserEntity userEntity);
}
