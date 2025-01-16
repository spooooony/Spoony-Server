package com.spoony.spoony_server.domain.location.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_gu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationGuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guId;
    private String guName;
    private String guAddress;
    private Double latitude;
    private Double longitude;

    @Builder
    public LocationGuEntity(Long guId, String guName, String guAddress, Double latitude, Double longitude) {
        this.guId = guId;
        this.guName = guName;
        this.guAddress = guAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
