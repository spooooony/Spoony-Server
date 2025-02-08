package com.spoony.spoony_server.application.port.command.place;

import com.spoony.spoony_server.adapter.dto.place.PlaceCheckRequestDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceCheckCommand {
    private final Long userId;
    private final Double latitude;
    private final Double longitude;
}
