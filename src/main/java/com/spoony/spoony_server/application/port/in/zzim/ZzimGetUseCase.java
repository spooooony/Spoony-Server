package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimFocusListResponseDTO;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetCardCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetFocusCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetLocationCardCommand;

public interface ZzimGetUseCase {
    ZzimCardListResponseDTO getZzimCardList(ZzimGetCardCommand command);
    ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command);
    ZzimCardListResponseDTO getZzimByLocation(ZzimGetLocationCardCommand command);
}
