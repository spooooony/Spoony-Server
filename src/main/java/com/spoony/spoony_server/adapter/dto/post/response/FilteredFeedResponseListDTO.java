package com.spoony.spoony_server.adapter.dto.post.response;

import com.spoony.spoony_server.adapter.dto.Cursor;

import java.util.List;

public record FilteredFeedResponseListDTO(
        List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
        String nextCursor // JSON 응답은 String 형태로 내보냄
) {
    public static FilteredFeedResponseListDTO of(
            List<FilteredFeedResponseDTO> filteredFeedResponseDTOList,
            Cursor nextCursor // 여기의 파라미터는 Cursor여야 함
    ) {
        return new FilteredFeedResponseListDTO(
                filteredFeedResponseDTOList,
                nextCursor != null ? nextCursor.toCursorString() : null
        );
    }
}
