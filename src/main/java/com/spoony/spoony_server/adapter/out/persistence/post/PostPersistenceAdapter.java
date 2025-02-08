package com.spoony.spoony_server.adapter.out.persistence.post;

import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.dto.post.CategoryType;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostPort {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final FollowRepository followRepository;
    private final MenuRepository menuRepository;
    private final PhotoRepository photoRepository;
    private final PostCategoryRepository postCategoryRepository;


    @Override
    public Optional<CategoryEntity> findByCategoryId(Long postId) {
        return categoryRepository.findById(postId);
    }

    @Override
    public List<CategoryEntity> findByCategoryType(CategoryType categoryType) {
        return categoryRepository.findByCategoryType(categoryType);
    }

    @Override
    public List<FollowEntity> findByFollowing(UserEntity following) {
        return followRepository.findByFollowing(following);
    }

    @Override
    public Optional<List<MenuEntity>> findMenuByPost(PostEntity postEntity) {
        return menuRepository.findByPost(postEntity);
    }

    @Override
    public Optional<List<PhotoEntity>> findPhotoByPost(PostEntity post) {
        return photoRepository.findByPost(post);
    }

    @Override
    public Optional<PhotoEntity> findFirstByPost(PostEntity postEntity) {
        return photoRepository.findFirstByPost(postEntity);
    }

    @Override
    public Optional<PostCategoryEntity> findByPost(PostEntity post) {
        return postCategoryRepository.findByPost(post);
    }

    @Override
    public List<PostEntity> findByUser(UserEntity userEntity) {
        return postRepository.findByUser(userEntity);
    }
}
