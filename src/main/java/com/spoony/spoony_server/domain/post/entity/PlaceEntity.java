package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "place")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer placeId;
    private Integer placeCode;
    private String placeName;
    private String placeAddress;
    private Double latitude;
    private Double longitude;
}
