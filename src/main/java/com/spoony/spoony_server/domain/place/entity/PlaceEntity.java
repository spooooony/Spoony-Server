package com.spoony.spoony_server.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer placeId;
    private Integer placeCode;
    private String placeName;
    private String placeAddress;
    private Double latitude;
    private Double longitude;

    @Builder
    public PlaceEntity(Integer placeId,
                       Integer placeCode,
                       String placeName,
                       String placeAddress,
                       Double latitude,
                       Double longitude) {
        this.placeId = placeId;
        this.placeCode = placeCode;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
