package com.spoony.spoony_server.adapter.dto.report;

import com.spoony.spoony_server.domain.report.ReportType;
import com.spoony.spoony_server.domain.report.UserReportType;

public record ReportUserRequestDTO(Long targetUserId,
                                   UserReportType userReportType,
                                   String reportDetail) {
}
