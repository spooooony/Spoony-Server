package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.adapter.dto.post.FilteredFeedResponseListDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedGetUseCase feedGetUseCase;

    @GetMapping("/following")
    @Operation(summary = "팔로잉 유저들의 피드를 최신순으로 조회", description = "팔로우한 유저들의 피드를 최신순으로 조회합니다.")
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFollowingFeed(@UserId Long userId){

        FollowingUserFeedGetCommand command = new FollowingUserFeedGetCommand(userId);
        FeedListResponseDTO feedListResponse = feedGetUseCase.getFeedListByFollowingUser(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }

    @GetMapping("/filtered")
    @Operation(
            summary = "탐색 탭 전체 피드 조회 API",
            description = """
        탐색 탭에서 최신순으로 전체 게시글(리뷰)을 조회하는 API입니다.
        
        - 정렬 기준: createdAt 기준 내림차순 (최신순)
        - 현재는 페이징 없이 전체를 반환합니다.
    """
    )
    public ResponseEntity<ResponseDTO<FilteredFeedResponseListDTO>> getFeeds(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) List<AgeGroup> ageGroups,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        Logger logger = LoggerFactory.getLogger(getClass());

        FilteredFeedResponseListDTO feedListResponse = null;
        //logger.info("🟢FilteredFeedResponseListDTO size: {}", feedListResponse.filteredFeedResponseDTOList().size());

        logger.info("getFeeds 호출됨");
        logger.info("categoryIds: {}", categoryIds);
        logger.info("regionIds: {}", regionIds);
        logger.info("ageGroups: {}", ageGroups);
        logger.info("sortBy: {}", sortBy);
        // 1. 기본값: categoryIds가 null 또는 비어 있으면 전체 카테고리(1)로 설정
        if (categoryIds == null || categoryIds.isEmpty()) {
            categoryIds = List.of(1L);
            logger.info("categoryIds가 null 또는 비어 있어 기본값으로 [1] 설정됨");
        }

        // 2. 카테고리 1과 2~9가 동시에 선택되면 예외 처리
        if (categoryIds.contains(1L) && categoryIds.size() > 1) {
            logger.error("카테고리 1은 다른 카테고리와 함께 선택할 수 없습니다.");
            throw new IllegalArgumentException("카테고리 전체(1)은 다른 카테고리와 함께 선택할 수 없습니다.");
        }

        // 3. 지역 필터링이 비활성화되면 null 처리
        if (regionIds != null && regionIds.isEmpty()) {
            regionIds = null;
            logger.info("regionIds가 비어있어 null로 설정됨");
        }

        // 로컬리뷰 여부 판단: categoryId = 2 포함 여부로 판단
        boolean isLocalReview = categoryIds.contains(2L);
        logger.info("🟢isLocalReview: {}", isLocalReview);

        // 4. ageGroups가 비어 있으면 null로 설정
        if (ageGroups != null && ageGroups.isEmpty()) {
            ageGroups = null;
            logger.info("ageGroups가 비어 있어 null로 설정됨");
        }
        // 4. FeedFilterCommand에 필터된 카테고리와 지역 정보, 정렬 기준을 전달
        FeedFilterCommand command = new FeedFilterCommand(categoryIds, regionIds, ageGroups, sortBy, isLocalReview);
        logger.info("🟢FeedFilterCommand 생성 완료: {}", command);
        // 5. 필터링된 피드를 가져오기 위해 UseCase 호출
        try {
            feedListResponse = feedGetUseCase.getFilteredFeed(command);
            logger.info("🟢FilteredFeedResponseListDTO size: {}", feedListResponse.filteredFeedResponseDTOList().size());
        } catch (Exception e) {
            logger.error("🟢피드 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }

        // 6. 응답 반환
        logger.info("🟢FilteredFeedResponseListDTO 반환");

        logger.info("🟢🟢🟢🟢🟢FilteredFeedResponseListDTO size: {}", feedListResponse.filteredFeedResponseDTOList().size());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }




}

