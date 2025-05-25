package com.spoony.spoony_server.adapter.dto.zzim.response;

import java.util.List;

public record ZzimCardListResponseDTO(int count, List<ZzimCardResponseDTO> zzimCardResponses) {

    public static ZzimCardListResponseDTO of(int count, List<ZzimCardResponseDTO> zzimCardResponses) {
        return new ZzimCardListResponseDTO(count, zzimCardResponses);
    }
}
