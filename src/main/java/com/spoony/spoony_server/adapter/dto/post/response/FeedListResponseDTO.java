package com.spoony.spoony_server.adapter.dto.post.response;

import java.util.List;

public record FeedListResponseDTO(List<FeedResponseDTO> feedResponseList) {

    public static FeedListResponseDTO of(List<FeedResponseDTO> feedResponseList) {
        return new FeedListResponseDTO(feedResponseList);
    }
}
