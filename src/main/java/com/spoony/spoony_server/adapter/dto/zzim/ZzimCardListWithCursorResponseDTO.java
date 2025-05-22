package com.spoony.spoony_server.adapter.dto.zzim;

import java.util.List;

public record ZzimCardListWithCursorResponseDTO(int count,
                                                List<ZzimCardResponseDTO> zzimCardResponses,
                                                Long nextCursor) {
}
