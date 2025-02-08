package com.spoony.spoony_server.application.port.in.place;

import com.spoony.spoony_server.adapter.dto.place.PlaceListResponseDTO;
import com.spoony.spoony_server.application.port.command.place.PlaceGetCommand;

public interface PlaceSearchUseCase {
    PlaceListResponseDTO getPlaceList(PlaceGetCommand command);
}
