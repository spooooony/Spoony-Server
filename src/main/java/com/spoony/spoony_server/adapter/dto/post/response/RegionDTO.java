package com.spoony.spoony_server.adapter.dto.post.response;

public record RegionDTO(long regionId, String regionName) {

    public static RegionDTO of(long regionId, String regionName) {
        return new RegionDTO(regionId, regionName);
    }
}

