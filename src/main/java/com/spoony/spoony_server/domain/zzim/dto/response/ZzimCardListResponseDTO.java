package com.spoony.spoony_server.domain.zzim.dto.response;

import java.util.List;

public record ZzimCardListResponseDTO(int count, List<ZzimCardResponseDTO> zzimCardResponses) {
}
