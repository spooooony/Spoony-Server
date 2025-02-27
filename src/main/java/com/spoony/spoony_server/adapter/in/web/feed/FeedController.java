package com.spoony.spoony_server.adapter.in.web.feed;

import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
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
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getFeedListByUserId(@UserId Long userId,
                                                                                @PathVariable long categoryId,
                                                                                @RequestParam(name = "query") String locationQuery,
                                                                                @RequestParam(name = "sortBy") String sortBy) {
        FeedGetCommand command = new FeedGetCommand(userId, categoryId, locationQuery, sortBy);
        FeedListResponseDTO feedListResponse = feedGetUseCase.getFeedListByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(feedListResponse));
    }
}
