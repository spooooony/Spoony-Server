package com.spoony.spoony_server.domain.location.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_station")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationStationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guId;
    private String guName;
    private String guAddress;
    private Double latitude;
    private Double longitude;

    @Builder
    public LocationStationEntity(Integer guId, String guName, String guAddress, Double latitude, Double longitude) {
        this.guId = guId;
        this.guName = guName;
        this.guAddress = guAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
