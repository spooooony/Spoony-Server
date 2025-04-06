package com.spoony.spoony_server.adapter.in.web.feed;

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

    @GetMapping("/search/user")
    @Operation(summary = "유저 검색 API", description = " 검색어를 통해 유저를 검색하는 API")

    public ResponseEntity<ResponseDTO<UserSearchResultListDTO>> searchUsers(){

    }
}