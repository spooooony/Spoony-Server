package com.spoony.spoony_server.adapter.out.persistence.location.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    private String locationName;
    private String locationAddress;
    private Double latitude;
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_type_id")
    private LocationTypeEntity locationTypeEntity;

    @Builder
    public LocationEntity(Long locationId,
                          String locationName,
                          String locationAddress,
                          Double latitude,
                          Double longitude,
                          LocationTypeEntity locationTypeEntity) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTypeEntity = locationTypeEntity;
    }
}
