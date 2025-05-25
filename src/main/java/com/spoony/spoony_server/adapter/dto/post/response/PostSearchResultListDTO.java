package com.spoony.spoony_server.adapter.dto.post.response;

import java.util.List;

public record PostSearchResultListDTO(List<FeedResponseDTO> postSearchResultList) {

    public static PostSearchResultListDTO of(List<FeedResponseDTO> postSearchResultList) {
        return new PostSearchResultListDTO(postSearchResultList);
    }
}
