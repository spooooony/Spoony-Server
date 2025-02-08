package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.dto.post.CategoryType;
import com.spoony.spoony_server.domain.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostPort {
    List<Post> findPostsByUser(Long userId);
}
