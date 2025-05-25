package com.spoony.spoony_server.adapter.dto.zzim.response;

import java.util.List;

public record ZzimFocusListResponseDTO(List<ZzimFocusResponseDTO> zzimFocusResponseList) {

    public static ZzimFocusListResponseDTO of(List<ZzimFocusResponseDTO> zzimFocusResponseList) {
        return new ZzimFocusListResponseDTO(zzimFocusResponseList);
    }
}
