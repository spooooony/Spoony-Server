package com.spoony.spoony_server.adapter.dto.zzim;

import java.util.List;

public record ZzimCardListResponseDTO(int count, List<ZzimCardResponseDTO> zzimCardResponses) {
}
