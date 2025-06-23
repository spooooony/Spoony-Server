package com.spoony.spoony_server.adapter.out.persistence.user;

import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.application.port.out.user.RegionPort;
import com.spoony.spoony_server.domain.user.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class RegionPersistenceAdapter implements RegionPort {

    private final RegionRepository regionRepository;

    @Override
    public Region findByAddress(String address) {
        String regionName = extractGuFromAddress(address);
        return regionRepository.findByRegionName(regionName)
                .map(RegionMapper::toDomain)
                .orElse(null);
    }

    private String extractGuFromAddress(String address) {
        String[] tokens = address.split(" ");
        for (String token : tokens) {
            if (token.endsWith("구")) {
                return token;
            }
        }
        return null;
    }
}
