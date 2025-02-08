package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionEntity;
import com.spoony.spoony_server.domain.user.Region;

public class RegionMapper {

    public static Region toDomain(final RegionEntity regionEntity) {
        return new Region(
                regionEntity.getRegionId(),
                regionEntity.getRegionName());
    }

}
