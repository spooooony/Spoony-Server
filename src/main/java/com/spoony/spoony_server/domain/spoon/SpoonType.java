package com.spoony.spoony_server.domain.spoon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SpoonType {
    private Long spoonTypeId;
    private String spoonName;
    private int spoonAmount;
    private BigDecimal probability;
    private String spoonImage;
    private String spoonGetImage;
}
