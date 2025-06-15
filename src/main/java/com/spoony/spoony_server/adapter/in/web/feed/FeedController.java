package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.adapter.dto.Cursor;
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
import com.spoony.spoony_server.global.util.CursorParser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @Operation(summary = "탐색 탭 전체 피드 조회 API", description = "탐색 탭 전체 피드를 조회합니다.")
    public ResponseEntity<ResponseDTO<FilteredFeedResponseListDTO>> getFeeds(
            @UserId Long currentUserId,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) List<AgeGroup> ageGroups,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {

        // 전체 카테고리
        if (categoryIds == null || categoryIds.isEmpty()) {
            categoryIds = List.of(1L);
        }

        // 카테고리 1과 2~9가 동시에 선택되면 예외 처리
        if (categoryIds.contains(1L) && categoryIds.size() > 1) {
            throw new BusinessException(PostErrorMessage.CATEGORY_SELECT);
        }

        // 지역 필터링이 비활성화되면 null 처리
        if (regionIds != null && regionIds.isEmpty()) {
            regionIds = null;
        }

        // 로컬리뷰 여부 판단: categoryId = 2 포함 여부로 판단
        boolean isLocalReview = categoryIds.contains(2L);

        if (ageGroups != null && ageGroups.isEmpty()) {
            ageGroups = null;
        }
        Cursor parsedCursor = null;
        if (cursor != null && !cursor.isBlank()) {
            parsedCursor = Cursor.fromCursorString(cursor);
        }
        FeedFilterCommand command = new FeedFilterCommand(categoryIds, regionIds, ageGroups, sortBy, isLocalReview, parsedCursor, size,currentUserId);
        FilteredFeedResponseListDTO feedListResponse;
        try {
            feedListResponse = feedGetUseCase.getFilteredFeed(command);
        } catch (Exception e) {
            throw new BusinessException(PostErrorMessage.POST_NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }
}
