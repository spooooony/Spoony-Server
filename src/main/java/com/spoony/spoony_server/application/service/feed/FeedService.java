package com.spoony.spoony_server.application.service.feed;

import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.application.port.dto.post.CategoryColorResponseDTO;
import com.spoony.spoony_server.application.port.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.application.port.dto.post.FeedResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.CategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.jpa.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.jpa.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostCategoryRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.jpa.ZzimPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService implements FeedGetUseCase {
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
                    else if (categoryId == 2) {
                        PostEntity postEntity = feedEntity.getPost();
                        return postEntity.getUser().getRegion().getRegionName().contains(location_query);
                    }
                    PostEntity postEntity = feedEntity.getPost();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));
                    return postCategoryEntity.getCategory().getCategoryId().equals(categoryId);
                })
                .map(feedEntity -> {
                    PostEntity postEntity = feedEntity.getPost();
                    UserEntity authorUserEntity = postEntity.getUser();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));
                    CategoryEntity categoryEntity = postCategoryEntity.getCategory();

                    return new FeedResponseDTO(
                            authorUserEntity.getUserId(),
                            authorUserEntity.getUserName(),
                            authorUserEntity.getRegion().getRegionName(),
                            postEntity.getPostId(),
                            postEntity.getTitle(),
                            new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
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