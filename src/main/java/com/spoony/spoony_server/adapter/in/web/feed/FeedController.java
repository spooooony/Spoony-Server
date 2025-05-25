package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.adapter.dto.post.response.FilteredFeedResponseListDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.FeedListResponseDTO;
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
    이 API는 탐색 탭에서 게시글(리뷰)을 조회하는 기능을 제공합니다. 게시글은 createdAt(작성일)을 기준으로 정렬되며, 정렬 기준을 선택할 수 있습니다.
    
    - sortBy 파라미터를 통해 게시글의 정렬 기준을 선택할 수 있습니다.
    - 기본값은 createdAt이며, 이는 최신순으로 게시글을 정렬합니다.
    - zzimCount을 선택하면, 타유저에 의해 저장된 횟수(zzimCount)가 많은 게시물이 먼저 보여집니다.

    ### 요청 파라미터
    - categoryIds: 필터링할 카테고리 ID 목록.
      - category 1은 단독으로만 사용할 수 있으며, 다른 카테고리와 함께 선택할 수 없습니다.(단, region, age, sort와는 별개로, 함께 사용 가능)
      - 예시: [1, 2, 3]은 카테고리 1과 2, 3을 선택하는 경우입니다.(에러 발생)
      - category 2은 로컬리뷰 필터링으로, 다른 categoryId와 함께 사용할 수 있습니다.
      - 예시: [2, 3]은 카테고리 2, 3을 선택하는 경우입니다. ('한식' 관련 로컬리뷰)
    - regionIds: 필터링할 지역 ID 목록.
      - 지역에 맞는 게시글을 조회할 수 있습니다. 이 값이 비어 있으면 필터가 적용되지 않습니다.
      - 예시: [101, 102]는 지역 101, 102에 해당하는 게시글을 필터링합니다.
    - ageGroups: 필터링할 연령대 목록.
      - 특정 연령대에 맞는 게시글을 조회할 수 있습니다. 이 값이 비어 있으면 필터가 적용되지 않습니다.
      - 예시: ageGroups=AGE_20S는 20대 연령 사용자로 필터링합니다.
      - 예시: ageGroups=AGE_20S, AGE_10S는 10대와 20대 연령 사용자로 필터링합니다.
    
    - sortBy: 정렬 기준.
      - 기본값은 createdAt으로 최신순으로 정렬됩니다.
      - zzimCount을 선택하면 타유저에 의해 저장된 횟수(zzimCount)가 많은 게시물이 먼저 보여집니다.
      - 예시: sortBy=createdAt은 최신순으로 정렬되고, sortBy=zzimCount은 저장된 횟수 순서대로 정렬됩니다.

    ### 요청 예시 URL:
    1. 최신순 (기본값) + 전체조회:
    GET /filtered

    2. 최신순 (기본값) + 전체조회:
    GET /filtered?categoryIds=1

    3. 최신순 (기본값) + 카테고리 필터링(로컬리뷰) + 지역 필터링:
    GET /filtered?categoryIds=2&regionIds=1,2

    4. 최신순 (기본값) + 카테고리 필터링(로컬리뷰(2), 한식(3)) + 연령대 필터링:
    GET /filtered?categoryIds=2,3&ageGroups=AGE_10S

    5. 저장순 (zzimCount) + 전체 카테고리 조회(categoryId=1) + 지역 필터링 + 연령대 필터링:
    GET /filtered?categoryIds=1,2&regionIds=1,2&ageGroups=AGE_20S&sortBy=zzimCount

    6. 카테고리 1과 2의 조합 오류 (카테고리 1은 다른 카테고리와 함께 선택할 수 없음):
    GET /filtered?categoryIds=1,2

    ### 오류 처리
    - 잘못된 categoryIds 값이 전달될 경우, IllegalArgumentException이 발생하며 400 Bad Request 응답이 반환됩니다.
    - 피드 조회 중 오류가 발생할 경우, BusinessException이 발생하며 500 Internal Server Error 응답이 반환됩니다.
    """
    )
    public ResponseEntity<ResponseDTO<FilteredFeedResponseListDTO>> getFeeds(
            @UserId Long currentUserId,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) List<AgeGroup> ageGroups,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
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
        FeedFilterCommand command = new FeedFilterCommand(categoryIds, regionIds, ageGroups, sortBy, isLocalReview, cursor, size,currentUserId);
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

