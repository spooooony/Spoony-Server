package com.spoony.spoony_server.application.port.in.location;

import com.spoony.spoony_server.adapter.dto.location.response.LocationResponseListDTO;
import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;

public interface LocationSearchUseCase {
    LocationResponseListDTO searchLocationsByQuery(LocationSearchCommand command);
}
