package com.spoony.spoony_server.domain.place;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Place {
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private String placeRoadAddress;
    private Double latitude;
    private Double longitude;

    public Place(String placeName, String placeAddress, String placeRoadAddress, Double latitude, Double longitude) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeRoadAddress = placeRoadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
