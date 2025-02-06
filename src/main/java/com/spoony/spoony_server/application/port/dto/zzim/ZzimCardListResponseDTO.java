package com.spoony.spoony_server.application.port.dto.zzim;

import java.util.List;

public record ZzimCardListResponseDTO(int count, List<ZzimCardResponseDTO> zzimCardResponses) {
}
