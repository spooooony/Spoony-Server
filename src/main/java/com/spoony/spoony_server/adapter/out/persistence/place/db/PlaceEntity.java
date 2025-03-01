package com.spoony.spoony_server.adapter.out.persistence.place.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private String placeRoadAddress;
    private Double latitude;
    private Double longitude;

    @Builder
    public PlaceEntity(Long placeId,
                       String placeName,
                       String placeAddress,
                       String placeRoadAddress,
                       Double latitude,
                       Double longitude) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeRoadAddress = placeRoadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
