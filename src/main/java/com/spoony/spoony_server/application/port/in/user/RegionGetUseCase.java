package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.RegionListDTO;

public interface RegionGetUseCase {
    RegionListDTO getRegionList();
}
