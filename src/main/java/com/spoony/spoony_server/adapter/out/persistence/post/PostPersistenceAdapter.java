package com.spoony.spoony_server.adapter.out.persistence.post;

import com.spoony.spoony_server.adapter.out.persistence.feed.PostSpecification;
import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionRepository;
import com.spoony.spoony_server.application.port.out.post.PhotoPort;
import com.spoony.spoony_server.domain.post.CategoryType;
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
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.CategoryErrorMessage;
import com.spoony.spoony_server.global.message.business.PlaceErrorMessage;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Adapter
@Transactional
@RequiredArgsConstructor
public class PostPersistenceAdapter implements
        PostPort,
        PostCategoryPort,
        CategoryPort,
        PhotoPort {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ScoopPostRepository scoopPostRepository;
    private final PhotoRepository photoRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findByUser_UserId(userId)
                .stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .map(PostMapper::toDomain)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
    }

    @Override
    public PostCategory findPostCategoryByPostId(Long postId) {
        return postCategoryRepository.findByPost_PostId(postId)
                .map(PostCategoryMapper::toDomain)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));
    }

    @Override
    public List<PostCategory> findPostCategoriesByPostId(Long postId) {
        return postCategoryRepository.findAllByPost_PostId(postId)
                .stream()
                .map(PostCategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostCategory> findAllByPostId(Long postId) {
        return postCategoryRepository.findAllByPost_PostId(postId).stream()
                .map(PostCategoryMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryMapper::toDomain)
                .orElseThrow(() -> new BusinessException(CategoryErrorMessage.CATEGORY_NOT_FOUND));
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return scoopPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public List<Photo> findPhotoById(Long postId) {
        return photoRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND))
                .stream()
                .map(PhotoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findMenuById(Long postId) {
        return menuRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.MENU_NOT_FOUND))
                .stream()
                .map(MenuMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Long savePost(Post post) {
        UserEntity userEntity = userRepository.findById(post.getUser().getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        PlaceEntity placeEntity = placeRepository.findById(post.getPlace().getPlaceId())
                .orElseThrow(() -> new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND));

        PostEntity postEntity = PostEntity.builder()
                .user(userEntity)
                .place(placeEntity)
                .description(post.getDescription())
                .value(post.getValue())
                .cons(post.getCons())
                .build();

        postRepository.save(postEntity);

        return postEntity.getPostId();
    }

    @Override
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

    @Override
    public void saveMenu(Menu menu) {
        PostEntity postEntity = postRepository.findById(menu.getPost().getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        MenuEntity menuEntity = MenuEntity.builder()
                .post(postEntity)
                .menuName(menu.getMenuName())
                .build();

        menuRepository.save(menuEntity);
    }

    @Override
    public void savePhoto(Photo photo) {
        PostEntity postEntity = postRepository.findById(photo.getPost().getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        PhotoEntity photoEntity = PhotoEntity.builder()
                .post(postEntity)
                .photoUrl(photo.getPhotoUrl())
                .build();

        photoRepository.save(photoEntity);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findFoodCategories() {
        return categoryRepository.findByCategoryType(CategoryType.FOOD).stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
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

    @Override
    public void deleteById(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public void updatePost(Long postId, String description, Double value, String cons) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        postEntity.updatePostContent(description, value, cons);
    }

    @Override
    public void deleteAllPostCategoryByPostId(Long postId) {
        List<PostCategoryEntity> postCategories = postCategoryRepository.findAllByPost_PostId(postId);
        postCategoryRepository.deleteAll(postCategories);
    }

    @Override
    public void deleteAllMenusByPostId(Long postId) {
        List<MenuEntity> menus = menuRepository.findAllByPost_PostId(postId);
        menuRepository.deleteAll(menus);
    }

    @Override
    public void deleteAllPhotosByPhotoUrl(List<String> deletePhotoUrlList) {
        photoRepository.deleteAllByPhotoUrlIn(deletePhotoUrlList);
    }


    @Override
    public List<Post> findByPostDescriptionContaining(String query) {
        List<PostEntity> postEntityList = postRepository.findByDescriptionContaining(query);
        return postEntityList.stream().map(PostMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Post> findAll() {
        List<PostEntity> postEntityList = postRepository.findAll();
        return postEntityList.stream().map(PostMapper::toDomain).collect(Collectors.toList());

    }
    @Transactional
    @Override
    public List<Post> findFilteredPosts(List<Long> categoryIds, List<Long> regionIds, List<AgeGroup> ageGroups, String sortBy, boolean isLocalReview,Long cursor, int size) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("🟢findFilteredPosts 호출됨");
        logger.info("🟢categoryIds: {}", categoryIds);
        logger.info("🟢regionIds: {}", regionIds);
        logger.info("🟢ageGroups: {}", ageGroups);
        logger.info("🟢isLocalReview: {}", isLocalReview);
        logger.info("🟢cursor: {}", cursor);
        logger.info("🟢size: {}", size);
        // 카테고리, 지역, 연령대, 로컬리뷰 필터 결합
        Specification<PostEntity> spec = PostSpecification.buildFilterSpec(
                categoryIds,
                regionIds,
                ageGroups,
                isLocalReview,
                sortBy,
                cursor
        );
        // 커서가 있을 경우 커서 기반 페이징 처리 추가
//        if (cursor != null) {
//            Specification<PostEntity> finalSpec = spec;  // Specification을 람다 내에서 사용할 수 있도록 final로 선언
//            spec = (root, query, cb) -> {
//                Predicate cursorPredicate = cb.lessThan(root.get("postId"), cursor);  // cursor보다 큰 postId 조회
//                return cb.and(cursorPredicate, finalSpec.toPredicate(root, query, cb));  // 기존 필터와 결합
//            };
//        }

        // Pageable 생성 (페이지 번호는 0부터 시작)
        Pageable pageable = PageRequest.of(0, size); // 기본적으로 최신순으로 정렬

        // 쿼리 실행 및 결과 반환 (페이징 처리)
        Page<PostEntity> page = postRepository.findAll(spec, pageable);

        // 엔티티를 도메인 객체로 변환 후 반환
        List<Post> result = page.getContent().stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());

        logger.info("🟢총 게시물 수: {}", page.getTotalElements());
        logger.info("🟢현재 페이지 게시물 수: {}", result.size());

        return result;

    }


    @Override
    public Long countPostsByUserId(Long userId) {
        return postRepository.countByUser_UserId(userId);

    }

    @Override
    public List<String> getPhotoUrls(Long postId) {
        return photoRepository.findAllByPost_PostId(postId).stream()
                .map(PhotoEntity::getPhotoUrl)
                .toList();
    }
}
