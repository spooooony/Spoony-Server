package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.adapter.dto.zzim.response.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.response.ZzimCardListWithCursorResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.response.ZzimFocusListResponseDTO;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetCardCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetFocusCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimGetLocationCardCommand;

public interface ZzimGetUseCase {
    ZzimCardListWithCursorResponseDTO getZzimCardList(ZzimGetCardCommand command);
    ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command);
    ZzimCardListResponseDTO getZzimByLocation(ZzimGetLocationCardCommand command);
}
