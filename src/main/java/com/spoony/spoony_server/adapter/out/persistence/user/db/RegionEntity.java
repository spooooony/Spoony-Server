package com.spoony.spoony_server.adapter.out.persistence.user.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "region")
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;
    private String regionName;

    @Builder
    public RegionEntity(Long regionId, String regionName) {
        this.regionId = regionId;
        this.regionName = regionName;
    }
}
