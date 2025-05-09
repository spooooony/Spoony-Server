package com.spoony.spoony_server.domain.user;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

@Getter
public enum AgeGroup {
    AGE_10S("10대", 10, 19),
    AGE_20S("20대", 20, 29),
    AGE_30S("30대", 30, 39),
    AGE_ETC("40대 이상", 40, 49);

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
