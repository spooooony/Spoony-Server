package com.spoony.spoony_server.application.port.in.place;

import com.spoony.spoony_server.application.port.dto.place.PlaceListResponseDTO;

public interface PlaceSearchUseCase {
    PlaceListResponseDTO getPlaceList(String query, int display);
}
