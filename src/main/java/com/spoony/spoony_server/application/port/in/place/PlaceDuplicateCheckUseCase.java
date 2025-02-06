package com.spoony.spoony_server.application.port.in.place;

import com.spoony.spoony_server.application.port.dto.place.PlaceCheckRequestDTO;

public interface PlaceDuplicateCheckUseCase {
    Boolean isDuplicate(PlaceCheckRequestDTO placeCheckRequestDTO);
}
