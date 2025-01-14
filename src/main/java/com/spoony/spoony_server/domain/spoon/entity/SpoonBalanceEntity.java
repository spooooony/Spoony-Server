package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spoon_balance")
public class SpoonBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private Integer amount;
    private LocalDateTime updatedAt;
}
