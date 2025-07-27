package com.spoony.spoony_server.adapter.out.persistence.report.mapper;

import com.spoony.spoony_server.adapter.out.persistence.report.db.UserReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.report.UserReport;

public class UserReportMapper {

    public static UserReport toDomain(UserReportEntity entity) {
        if (entity == null) return null;
        return new UserReport(
                entity.getUserReportId(),
                entity.getUserReportType(),
                entity.getUserReportDetail(),
                UserMapper.toDomain(entity.getReporter()),
                UserMapper.toDomain(entity.getTargetUser())
        );
    }

    public static UserReportEntity toEntity(UserReport domain) {
        if (domain == null) return null;
        return UserReportEntity.builder()
                .userReportId(domain.getUserReportId())
                .userReportType(domain.getUserReportType())
                .userReportDetail(domain.getUserReportDetail())
                .reporter(UserMapper.toEntity(domain.getReporter()))
                .targetUser(UserMapper.toEntity(domain.getTargetUser()))
                .build();
    }
}