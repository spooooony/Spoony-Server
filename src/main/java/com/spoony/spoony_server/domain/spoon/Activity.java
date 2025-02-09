package com.spoony.spoony_server.domain.spoon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Activity {
    private Long activityId;
    private String activityName;
    private Integer changeAmount;
}
