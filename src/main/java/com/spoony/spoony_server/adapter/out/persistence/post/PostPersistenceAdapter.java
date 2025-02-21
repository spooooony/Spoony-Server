package com.spoony.spoony_server.adapter.out.persistence.post;

import com.spoony.spoony_server.adapter.dto.post.CategoryType;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.*;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ScoopPostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.post.CategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.CategoryErrorMessage;
import com.spoony.spoony_server.global.message.PlaceErrorMessage;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostPersistenceAdapter implements
        PostPort,
        PostCategoryPort,
        CategoryPort {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ScoopPostRepository scoopPostRepository;
    private final PhotoRepository photoRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public List<Post> findUserByUserId(Long userId) {
        return postRepository.findByUser_UserId(userId)
                .stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .map(PostMapper::toDomain)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
    }

    public PostCategory findPostCategoryByPostId(Long postId) {
        return postCategoryRepository.findByPost_PostId(postId)
                .map(PostCategoryMapper::toDomain)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));
    }

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryMapper::toDomain)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));
    }

    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return scoopPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    public List<Photo> findPhotoById(Long postId) {
        return photoRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND))
                .stream()
                .map(PhotoMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Menu> findMenuById(Long postId) {
        return menuRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.MENU_NOT_FOUND))
                .stream()
                .map(MenuMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Long savePost(Post post) {
        UserEntity userEntity = userRepository.findById(post.getUser().getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        PlaceEntity placeEntity = placeRepository.findById(post.getPlace().getPlaceId())
                .orElseThrow(() -> new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND));

        PostEntity postEntity = PostEntity.builder()
                .user(userEntity)
                .place(placeEntity)
                .title(post.getTitle())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        postRepository.save(postEntity);

        return postEntity.getPostId();
    }

    public void savePostCategory(PostCategory postCategory) {
        PostEntity postEntity = postRepository.findById(postCategory.getPost().getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        CategoryEntity categoryEntity = categoryRepository.findById(postCategory.getCategory().getCategoryId())
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));

        PostCategoryEntity postCategoryEntity = PostCategoryEntity.builder()
                .post(postEntity)
                .category(categoryEntity)
                .build();

        postCategoryRepository.save(postCategoryEntity);
    }

    public void saveMenu(Menu menu) {
        PostEntity postEntity = postRepository.findById(menu.getPost().getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        MenuEntity menuEntity = MenuEntity.builder()
                .post(postEntity)
                .menuName(menu.getMenuName())
                .build();

        menuRepository.save(menuEntity);
    }

    public void savePhoto(Photo photo) {
        PostEntity postEntity = postRepository.findById(photo.getPost().getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        PhotoEntity photoEntity = PhotoEntity.builder()
                .post(postEntity)
                .photoUrl(photo.getPhotoUrl())
                .build();

        photoRepository.save(photoEntity);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    public List<Category> findFoodCategories() {
        return categoryRepository.findByCategoryType(CategoryType.FOOD).stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }


    public void saveScoopPost(User user, Post post) {
        UserEntity userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        PostEntity postEntity = postRepository.findById(post.getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        ScoopPostEntity scoopPostEntity = ScoopPostEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .build();

        scoopPostRepository.save(scoopPostEntity);
    }
}
