package com.spoony.spoony_server.application.service.location;

import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.adapter.dto.location.LocationResponseDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationTypeDTO;
import com.spoony.spoony_server.application.port.out.location.LocationPort;
import com.spoony.spoony_server.domain.location.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService implements
        LocationSearchUseCase {

    private final LocationPort locationPort;

    public LocationResponseListDTO searchLocationsByQuery(LocationSearchCommand command) {

        List<Location> locationList = locationPort.findByLocationNameContaining(command.getQuery());

        List<LocationResponseDTO> locationResponseList = locationList.stream()
                .map(location -> new LocationResponseDTO(
                        location.getLocationId(),
                        location.getLocationName(),
                        location.getLocationAddress(),
                        new LocationTypeDTO(
                                location.getLocationType().getLocationTypeId(),
                                location.getLocationType().getLocationTypeName(),
                                location.getLocationType().getScope()
                        ),
                        location.getLongitude(),
                        location.getLatitude()
                ))
                .toList();

        return new LocationResponseListDTO(locationResponseList);
    }
}
