package com.spoony.spoony_server.application.port.in.feed;

import com.spoony.spoony_server.application.port.dto.post.FeedListResponseDTO;

public interface FeedGetUseCase {
    FeedListResponseDTO getFeedListByUserId(Long userId, Long categoryId, String location_query, String sortBy);
}
