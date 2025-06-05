package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spoon_type")
public class SpoonTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spoonTypeId;

    @Column(nullable = false, length = 50)
    private String spoonName;

    @Column(nullable = false)
    private int spoonAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal probability;

    @Column(nullable = false)
    private String spoonImage;

    @Column(nullable = false)
    private String spoonGetImage;
}
