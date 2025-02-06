package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.application.port.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.application.port.dto.zzim.ZzimFocusListResponseDTO;

public interface ZzimGetUseCase {
    ZzimCardListResponseDTO getZzimCardList(Long userId);
    ZzimFocusListResponseDTO getZzimFocusList(Long userId, Long placeId);
    ZzimCardListResponseDTO getZzimByLocation(Long userId, Long locationId);
}
