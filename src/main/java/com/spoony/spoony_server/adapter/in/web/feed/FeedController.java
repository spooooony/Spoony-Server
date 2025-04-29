package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.adapter.dto.user.UserSearchResultListDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedGetUseCase feedGetUseCase;



    @GetMapping("/{categoryId}")
    @Operation(summary = "피드 조회 API", description = "사용자의 피드를 카테고리 단위로 조회하는 API")
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFeedListByUserId(
            @UserId Long userId,
            @PathVariable long categoryId,
            @RequestParam(name = "query") String locationQuery,
            @RequestParam(name = "sortBy") String sortBy) {
        FeedGetCommand command = new FeedGetCommand(userId, categoryId, locationQuery, sortBy);
        FeedListResponseDTO feedListResponse = feedGetUseCase.getFeedListByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }

    @GetMapping()
    @Operation(
            summary = "탐색 탭 전체 피드 조회 API",
            description = """
    탐색 탭에서 최신순으로 전체 게시글(리뷰)을 조회하는 API입니다.

    - 정렬 기준: createdAt 기준 내림차순 (최신순)
    - 현재는 페이징 없이 전체를 반환합니다.
    """
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getLatestReviewFeed() {
        FeedListResponseDTO feedListResponse = feedGetUseCase.getAllPosts();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }
    @GetMapping("/following")
    @Operation(
            summary = "팔로잉 유저들의 피드 조회 API",
            description = """
    내가 팔로우하고 있는 유저들의 최신 게시글(리뷰)을 조회하는 API입니다.

    - 정렬 기준: createdAt 기준 내림차순 (최신순)
    - 현재는 페이징 없이 전체를 반환합니다.
    """
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFollowingUserFeed(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        FeedListResponseDTO feedListResponse = feedGetUseCase.getPostsFromFollowingUsers(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }
}