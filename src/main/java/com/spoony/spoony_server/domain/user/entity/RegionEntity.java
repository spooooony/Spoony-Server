package com.spoony.spoony_server.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "region")
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer regionId;
    private String regionName;
}
