package com.spoony.spoony_server.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer regionId;
    private String regionName;
}
