package com.spoony.spoony_server.adapter.dto.post.response;

import java.util.List;

public record FilteredFeedResponseListDTO(
        List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
        String nextCursor // JSON 응답은 String 형태로 내보냄
) {
    public static FilteredFeedResponseListDTO of(
            List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
            String nextCursor // 여기의 파라미터는 Cursor여야 함
    ) {
        return new FilteredFeedResponseListDTO(
                filteredFeedResponseDTOList,
                nextCursor
        );
    }
}
