package com.spoony.spoony_server.adapter.out.persistence.location;

import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationRepository;
import com.spoony.spoony_server.adapter.out.persistence.location.mapper.LocationMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.location.LocationPort;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LocationPersistenceAdapter implements LocationPort {

    private final LocationRepository locationRepository;



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
