package com.spoony.spoony_server.domain.post.service;


import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.post.dto.response.PostResponseDTO;
import com.spoony.spoony_server.domain.post.entity.CategoryEntity;
import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.CategoryRepository;
import com.spoony.spoony_server.domain.post.repository.MenuRepository;
import com.spoony.spoony_server.domain.post.repository.PostCategoryRepository;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.user.entity.RegionEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public PostResponseDTO getPostById(Integer postId) {

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        UserEntity userEntity = postEntity.getUser();
        if (userEntity == null) {
            throw new BusinessException(UserErrorMessage.NOT_FOUND_ERROR);
        }

        RegionEntity regionEntity = userRepository.findReigonByUserId(userEntity.getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));
        PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        Integer categoryId = postCategoryEntity.getCategory().getCategoryId();
        CategoryEntity categoryEntity = categoryRepository.findByCategoryId(categoryId);

        PlaceEntity place = postEntity.getPlace();
        LocalDateTime latestDate = postEntity.getUpdatedAt().isAfter(postEntity.getCreatedAt()) ? postEntity.getUpdatedAt() : postEntity.getCreatedAt();
        String formattedDate = latestDate.toLocalDate().toString();
        Integer zzim_count = postRepository.countByPostId(postId);
        List<String> category_list = categoryEntity.getCategoryName();


        List<MenuEntity> menuEntityList = menuRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));

        List<String> menuList = menuEntityList.stream()
                .map(menuEntity -> menuEntity.getMenuName())
                .collect(Collectors.toList());

        return new PostResponseDTO(postId, userEntity.getUserId(), userEntity.getUserName(), regionEntity.getRegionName(), category_list, postEntity.getTitle(), formattedDate, menuList, postEntity.getDescription(),
                place.getPlaceName(), place.getPlaceAddress(), place.getLatitude(), place.getLongitude(), zzim_count

        );
    }
}
