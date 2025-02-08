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
}
