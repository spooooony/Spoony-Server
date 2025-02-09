package com.spoony.spoony_server.adapter.out.persistence.spoon.mapper;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ActivityEntity;
import com.spoony.spoony_server.domain.spoon.Activity;

public class ActivityMapper {

    public static Activity toDomain(ActivityEntity activityEntity) {

        return new Activity(
                activityEntity.getActivityId(),
                activityEntity.getActivityName(),
                activityEntity.getChangeAmount()
        );
    }
}
