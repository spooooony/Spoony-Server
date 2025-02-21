package com.spoony.spoony_server.application.port.in.place;

import com.spoony.spoony_server.application.port.command.place.PlaceCheckCommand;

public interface PlaceDuplicateCheckUseCase {
    Boolean isDuplicate(PlaceCheckCommand command);
}
