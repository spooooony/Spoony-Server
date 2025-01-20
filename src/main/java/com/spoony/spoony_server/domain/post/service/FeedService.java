package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.FeedListResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.FeedResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.RegionDTO;
import com.spoony.spoony_server.domain.post.entity.CategoryEntity;
import com.spoony.spoony_server.domain.post.entity.FeedEntity;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.*;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//
//@Service
//@RequiredArgsConstructor
//public class FeedService {
//
//    private final UserRepository userRepository;
//    private final FeedRepository feedRepository;
//    private final PostRepository postRepository;
//    private final CategoryRepository categoryRepository;
//    private final PostCategoryRepository postCategoryRepository;
//    private final ZzimPostRepository zzimPostRepository;
//
////    public List<FeedResponseDTO> getFeedListByUserId(Long userId, String location_query, Long categoryId) {
////
////
////        // 특정 사용자의 피드 리스트 가져오기
////        List<FeedEntity> feedEntityListByUserId = feedRepository.findByUser_UserId(userId);
////
////        feedEntityListByUserId.stream().filter(feedEntity -> {
////                    PostEntity postEntity = feedEntity.getPost();
////
////                    if (postEntity == null || postEntity.getPlace() == null || postEntity.getPlace().getPlaceAddress() == null) {
////                        return false; // Null 값은 제외
////                    }
////                    // 지역 필터링
////                    return postEntity.getPlace().getPlaceAddress().contains(location_query);
////                })
////                .forEach(feedEntity -> uniqueFeedMap.put(feedEntity.getFeedId(), feedEntity)); // 중복 제거 및 Map에 추가
////
////
////        // 2차 필터링: 카테고리 기준
////        List<FeedResponseDTO> feedResponses = uniqueFeedMap.values().stream()
////                .filter(feedEntity -> {
////                    PostEntity post = feedEntity.getPost();
////                    if (post == null) {
////                        return false; // Null 값은 제외
////                    }
////                    // 카테고리 필터링
////                    return postCategoryRepository.findByPost(post)
////                            .map(postCategory -> postCategory.getCategory().getCategoryId().equals(categoryId))
////                            .orElse(false);
////                })
////                .map(feedEntity -> {
////                    PostEntity postEntity = feedEntity.getPost();
////
////                    // 유저 정보
////                    Long postUserId = postEntity.getUser().getUserId();
////                    String postUserName = postEntity.getUser().getUserName();
////                    RegionEntity postUserRegion = postEntity.getUser().getRegion();
////
////                    // 카테고리 정보
////                    PostCategoryEntity postCategoryEntity = postCategoryRepository
////                            .findByPost(postEntity)
////                            .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
////
////                    CategoryEntity categoryEntity = categoryRepository.findByCategoryId(categoryId);
////                    String categoryName = categoryEntity.getCategoryName();
////                    String iconUrlColor = categoryEntity.getIconUrlColor();
////                    String backgroundColor = categoryEntity.getBackgroundColor();
////
////                    // 찜 리스트에 등록한 횟수
////                    Long zzimCount = zzimPostRepository.countByPost(postEntity);
////
////                    // FeedResponseDTO 생성
////                    return new FeedResponseDTO(
////                            postUserId,
////                            postUserName,
////                            postUserRegion,
////                            postEntity.getTitle(),
////                            new CategoryColorResponseDTO(categoryName, iconUrlColor, backgroundColor),
////                            zzimCount
////                    );
////                })
////                .toList();
////
////        return feedResponses;
////    }
////}
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final ZzimPostRepository zzimPostRepository;


    public FeedListResponseDTO getFeedListByUserId(Long userId, String location_query, String sortBy) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        List<FeedEntity> feedEntityList = feedRepository.findByUser(userEntity);

        List<FeedResponseDTO> feedResponseList = feedEntityList.stream()
                .filter(feedEntity -> feedEntity.getPost().getPlace().getPlaceAddress().contains(location_query))
                .map(feedEntity -> {
                    PostEntity postEntity = feedEntity.getPost();
                    UserEntity authorUserEntity = postEntity.getUser();
                    PostCategoryEntity postCategoryEntity = postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
                    CategoryEntity eachCategoryEntity = postCategoryEntity.getCategory();


                    return new FeedResponseDTO(
                            authorUserEntity.getUserId(),
                            authorUserEntity.getUserName(),
                            postEntity.getCreatedAt(), new RegionDTO(authorUserEntity.getRegion().getRegionId(), authorUserEntity.getRegion().getRegionName()),
                            postEntity.getTitle(),

                            new CategoryColorResponseDTO(
                                    postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND)).getCategory().getCategoryName(),
                                    postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND)).getCategory().getIconUrlColor(),
                                    postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND)).getCategory().getTextColor(),
                                    postCategoryRepository.findByPost(postEntity).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND)).getCategory().getBackgroundColor()), zzimPostRepository.countByPost(postEntity));
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