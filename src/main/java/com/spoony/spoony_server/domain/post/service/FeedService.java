package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.FeedListResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.FeedResponseDTO;
import com.spoony.spoony_server.domain.post.entity.CategoryEntity;
import com.spoony.spoony_server.domain.post.entity.FeedEntity;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.FeedRepository;
import com.spoony.spoony_server.domain.post.repository.PostCategoryRepository;
import com.spoony.spoony_server.domain.post.repository.ZzimPostRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final ZzimPostRepository zzimPostRepository;

    public FeedListResponseDTO getFeedListByUserId(Long userId, Long categoryId, String location_query, String sortBy) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        List<FeedEntity> feedEntityList = feedRepository.findByUser(userEntity);

        List<FeedResponseDTO> feedResponseList = feedEntityList.stream()
                .filter(feedEntity -> feedEntity.getPost().getPlace().getPlaceAddress().contains(location_query))
                .filter(feedEntity -> {
                    if (categoryId == 1) {
                        return true;
                    }
                    PostEntity postEntity = feedEntity.getPost();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
                    return postCategoryEntity.getCategory().getCategoryId().equals(categoryId);
                })
                .map(feedEntity -> {
                    PostEntity postEntity = feedEntity.getPost();
                    UserEntity authorUserEntity = postEntity.getUser();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
                    CategoryEntity categoryEntity = postCategoryEntity.getCategory();

                    return new FeedResponseDTO(
                            authorUserEntity.getUserId(),
                            authorUserEntity.getUserName(),
                            authorUserEntity.getRegion().getRegionName(),
                            postEntity.getPostId(),
                            postEntity.getTitle(),
                            new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ),
                            zzimPostRepository.countByPost(postEntity),
                            postEntity.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        if (sortBy.equals("popularity")) {
            feedResponseList.sort((dto1, dto2) -> Long.compare(dto2.zzimCount(), dto1.zzimCount()));
        } else if (sortBy.equals("latest")) {
            feedResponseList.sort((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()));
        }

        return new FeedListResponseDTO(feedResponseList);
    }
}