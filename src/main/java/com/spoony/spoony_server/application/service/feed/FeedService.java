package com.spoony.spoony_server.application.service.feed;

import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.post.db.CategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
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

    public FeedListResponseDTO getFeedListByUserId(FeedGetCommand command) {

        String locationQuery = command.getLocationQuery();
        String sortBy = command.getSortBy();

        UserEntity userEntity = userRepository.findById(command.getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        List<FeedEntity> feedEntityList = feedRepository.findByUser(userEntity);

        List<FeedResponseDTO> feedResponseList = feedEntityList.stream()
                .filter(feedEntity -> feedEntity.getPost().getPlace().getPlaceAddress().contains(locationQuery))
                .filter(feedEntity -> {
                    if (command.getCategoryId() == 1) {
                        return true;
                    }
                    else if (command.getCategoryId() == 2) {
                        PostEntity postEntity = feedEntity.getPost();
                        return postEntity.getUser().getRegion().getRegionName().contains(locationQuery);
                    }
                    PostEntity postEntity = feedEntity.getPost();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));
                    return postCategoryEntity.getCategory().getCategoryId().equals(command.getCategoryId());
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