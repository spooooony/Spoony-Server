package com.spoony.spoony_server.application.port.in.location;

import com.spoony.spoony_server.application.port.dto.location.LocationResponseListDTO;

public interface LocationSearchUseCase {
    LocationResponseListDTO searchLocationsByQuery(String query);
}
