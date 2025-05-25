package com.spoony.spoony_server.adapter.dto.post.response;

import java.util.List;

public record FilteredFeedResponseListDTO(List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
                                          Long nextCursor) {

    public static FilteredFeedResponseListDTO of(List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
                                                 Long nextCursor) {
        return new FilteredFeedResponseListDTO(filteredFeedResponseDTOList, nextCursor);
    }
}
