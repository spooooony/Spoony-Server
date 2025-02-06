package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.post.FeedListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedGetUseCase feedGetUseCase;

    @GetMapping("/{userId}/{categoryId}")
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFeedListByUserId(@PathVariable Long userId,
                                                                                @PathVariable Long categoryId,
                                                                                @RequestParam(name = "query") String locationQuery,
                                                                                @RequestParam(name = "sortBy") String sortBy) {
        FeedListResponseDTO feedListResponse = feedGetUseCase.getFeedListByUserId(userId, categoryId, locationQuery, sortBy);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }
}
