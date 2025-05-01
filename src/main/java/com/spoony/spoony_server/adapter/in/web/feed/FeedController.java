package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.adapter.dto.user.UserSearchResultListDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
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

    @GetMapping("following")
    @Operation(summary = "팔로잉 유저들의 피드를 최신순으로 조회", description = "팔로우한 유저들의 피드를 최신순으로 조회합니다.")
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFollowingFeed(@UserId Long userId){

        FollowingUserFeedGetCommand command = new FollowingUserFeedGetCommand(userId);
        FeedListResponseDTO feedListResponse = feedGetUseCase.getFeedListByFollowingUser(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }

    @GetMapping("")
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

}