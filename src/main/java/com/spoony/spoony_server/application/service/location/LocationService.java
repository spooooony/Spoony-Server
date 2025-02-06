package com.spoony.spoony_server.application.service.location;

import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.application.port.dto.location.LocationResponseDTO;
import com.spoony.spoony_server.application.port.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.application.port.dto.location.LocationTypeDTO;
import com.spoony.spoony_server.adapter.out.persistence.location.jpa.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.jpa.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService implements
        LocationSearchUseCase {

    private final LocationRepository locationRepository;

    public LocationResponseListDTO searchLocationsByQuery(String query) {
        List<LocationEntity> locationEntityList = locationRepository.findByLocationNameContaining(query);

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
