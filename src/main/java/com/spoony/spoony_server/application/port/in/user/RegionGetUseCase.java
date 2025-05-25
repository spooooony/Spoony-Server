package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.response.RegionListResponseDTO;

public interface RegionGetUseCase {
    RegionListResponseDTO getRegionList();
}
