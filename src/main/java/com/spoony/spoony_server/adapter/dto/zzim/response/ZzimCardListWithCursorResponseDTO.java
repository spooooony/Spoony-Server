package com.spoony.spoony_server.adapter.dto.zzim.response;

import java.util.List;

public record ZzimCardListWithCursorResponseDTO(int count,
                                                List<ZzimCardResponseDTO> zzimCardResponses,
                                                Long nextCursor) {

    public static ZzimCardListWithCursorResponseDTO of(int count,
                                                       List<ZzimCardResponseDTO> zzimCardResponses,
                                                       Long nextCursor) {
        return new ZzimCardListWithCursorResponseDTO(count, zzimCardResponses, nextCursor);
    }
}
