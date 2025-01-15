package com.spoony.spoony_server.domain.location.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_dong")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationDongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guId;
    private String guName;
    private String guAddress;
    private Double latitude;
    private Double longitude;

    @Builder
    public LocationDongEntity(String guName, Integer guId, String guAddress, Double latitude, Double longitude) {
        this.guName = guName;
        this.guId = guId;
        this.guAddress = guAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
