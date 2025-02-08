package com.spoony.spoony_server.application.service.location;

import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.adapter.dto.location.LocationResponseDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationTypeDTO;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService implements
        LocationSearchUseCase {

    private final LocationRepository locationRepository;

    public LocationResponseListDTO searchLocationsByQuery(LocationSearchCommand command) {

        List<LocationEntity> locationEntityList = locationRepository.findByLocationNameContaining(command.getQuery());

        List<LocationResponseDTO> locationResponseList = locationEntityList.stream()
                .map(locationEntity -> new LocationResponseDTO(
                        locationEntity.getLocationId(),
                        locationEntity.getLocationName(),
                        locationEntity.getLocationAddress(),
                        new LocationTypeDTO(
                                locationEntity.getLocationTypeEntity().getLocationTypeId(),
                                locationEntity.getLocationTypeEntity().getLocationTypeName(),
                                locationEntity.getLocationTypeEntity().getScope()
                        ),
                        locationEntity.getLongitude(),
                        locationEntity.getLatitude()
                ))
                .toList();

        return new LocationResponseListDTO(locationResponseList);
    }
}
