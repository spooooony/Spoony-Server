package com.spoony.spoony_server.adapter.out.persistence.post;

import com.spoony.spoony_server.adapter.out.persistence.place.mapper.PlaceMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.dto.post.CategoryType;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostPort {

    private final PostRepository postRepository;

    @Override
    public List<Post> findPostsByUser(Long userId) {
        List<PostEntity> postEntities = postRepository.findByUser_UserId(userId);
        return postEntities.stream()
                .map(postEntity -> new Post(
                        postEntity.getPostId(),
                        UserMapper.toDomain(postEntity.getUser()),
                        PlaceMapper.toDomain(postEntity.getPlace()),
                        postEntity.getTitle(),
                        postEntity.getDescription(),
                        postEntity.getCreatedAt(),
                        postEntity.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
