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
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.CategoryErrorMessage;
import com.spoony.spoony_server.global.message.business.PlaceErrorMessage;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
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
    private final RegionRepository regionRepository;

    @Override
    public List<Post> findPostsByUserId (Long userId) {
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


//    @Transactional
//    @Override
//    public List<Post> findFilteredPosts(List<Long> categoryIds , List<Long> regionIds) {
//        Logger logger = LoggerFactory.getLogger(getClass()); // 클래스마다 로거 생성
//        logger.info("findFilteredPosts 호출됨");
//
//        // 카테고리 및 지역 필터 결합
//        Specification<PostEntity> spec = PostSpecification.withCategoryAndRegion(categoryIds, regionIds);
//
//        // 로컬리뷰 필터 (category_id = 2인 경우 작성자의 지역과 게시물 지역 일치)
//        spec = spec.and(PostSpecification.withLocalReview(categoryIds));
//
//        // 쿼리 실행 및 정렬 (최신순으로 createdAt 기준)
//        List<PostEntity> filteredPostEntities = postRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//        logger.info("필터링된 게시물 수: {}", filteredPostEntities.size());
//
//        // 엔티티를 도메인 객체로 변환 후 반환
//        return filteredPostEntities.stream()
//                .map(PostMapper::toDomain)
//                .collect(Collectors.toList());
//    }

    @Transactional
    @Override
    public List<Post> findFilteredPosts(List<Long> categoryIds, List<Long> regionIds, String sortBy) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("findFilteredPosts 호출됨");
        logger.info("categoryIds: {}", categoryIds);
        logger.info("regionIds: {}", regionIds);

        // 카테고리 및 지역 필터 결합
        Specification<PostEntity> spec = PostSpecification.withCategoryAndRegion(categoryIds, regionIds);

        // 쿼리 실행 및 정렬
        List<PostEntity> filteredPostEntities = postRepository.findAll(spec, Sort.by(Sort.Direction.DESC, sortBy));
        logger.info("findAll 실행 완료. 필터링된 게시물 수: {}", filteredPostEntities.size());

        // 엔티티를 도메인 객체로 변환 후 반환
        List<Post> result = filteredPostEntities.stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
        logger.info("도메인 객체로 변환 완료. 반환할 게시물 수: {}", result.size());

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
