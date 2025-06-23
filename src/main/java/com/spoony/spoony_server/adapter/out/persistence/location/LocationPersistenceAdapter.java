package com.spoony.spoony_server.adapter.out.persistence.location;

import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationRepository;
import com.spoony.spoony_server.adapter.out.persistence.location.mapper.LocationMapper;
import com.spoony.spoony_server.application.port.out.location.LocationPort;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Adapter
@Transactional
@RequiredArgsConstructor
public class LocationPersistenceAdapter implements LocationPort {

    private final LocationRepository locationRepository;

    @Override
    public Location findLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .map(LocationMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public List<Location> findByLocationNameContaining(String query) {
        List<LocationEntity> locationEntityList = locationRepository.findByLocationNameContaining(query);
        return locationEntityList.stream()
                .map(LocationMapper::toDomain)
                .collect(Collectors.toList());
    }
}
