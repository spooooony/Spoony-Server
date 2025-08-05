package com.spoony.spoony_server.adapter.out.persistence.report.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PostMapper;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.report.Report;

public class ReportMapper {

    public static Report toDomain(ReportEntity entity) {
        if (entity == null) return null;

        return new Report(
                entity.getReportId(),
                entity.getReportType(),
                entity.getReportDetail(),
                PostMapper.toDomain(entity.getPost()),
                UserMapper.toDomain(entity.getUser()),
                entity.getCreatedAt()
        );
    }
}