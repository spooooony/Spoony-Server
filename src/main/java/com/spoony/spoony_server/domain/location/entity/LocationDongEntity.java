package com.spoony.spoony_server.domain.location.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "location_dong")
public class LocationDongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guId;
    private String guName;
    private String guAddress;
    private Double latitude;
    private Double longitude;
}
