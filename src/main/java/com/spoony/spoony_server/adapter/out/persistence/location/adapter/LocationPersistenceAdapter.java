package com.spoony.spoony_server.adapter.out.persistence.location.adapter;

import com.spoony.spoony_server.adapter.out.persistence.location.jpa.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.jpa.LocationRepository;
import com.spoony.spoony_server.application.port.out.location.LocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocationPersistenceAdapter implements LocationPort {

    private final LocationRepository locationRepository;

    @Override
    public List<LocationEntity> findByLocationNameContaining(String query) {
        return locationRepository.findByLocationNameContaining(query);
    }
}
