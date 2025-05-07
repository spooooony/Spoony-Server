package com.spoony.spoony_server.domain.user;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

@Getter
public enum AgeGroup {
    AGE_20S("20대", 20, 29),
    AGE_30S("30대", 30, 39),
    AGE_40S("40대", 40, 49),
    AGE_50S("50대", 50, 59);

    private final String displayName;
    private final int minAge;
    private final int maxAge;

    AgeGroup(String displayName, int minAge, int maxAge) {
        this.displayName = displayName;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public static AgeGroup from(LocalDate birth) {
        int age = Period.between(birth, LocalDate.now()).getYears();
        return Arrays.stream(values())
                .filter(g -> age >= g.minAge && age <= g.maxAge)
                .findFirst()
                .orElse(null);
    }
}
