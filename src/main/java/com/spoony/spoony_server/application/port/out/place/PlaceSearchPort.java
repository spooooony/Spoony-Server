package com.spoony.spoony_server.application.port.out.place;

import com.spoony.spoony_server.adapter.dto.place.response.PlaceListResponseDTO;

public interface PlaceSearchPort {
    PlaceListResponseDTO getPlaceList(String query, int display);
}
