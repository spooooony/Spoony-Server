package com.spoony.spoony_server.domain.location.service;

import com.spoony.spoony_server.domain.location.dto.response.LocationResponseDTO;
import com.spoony.spoony_server.domain.location.dto.response.LocationResponseListDTO;
import com.spoony.spoony_server.domain.location.dto.response.LocationTypeDTO;
import com.spoony.spoony_server.domain.location.entity.LocationEntity;
import com.spoony.spoony_server.domain.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

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
