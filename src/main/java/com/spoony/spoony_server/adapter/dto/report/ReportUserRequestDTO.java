package com.spoony.spoony_server.adapter.dto.report;

import com.spoony.spoony_server.domain.report.ReportType;

public record ReportUserRequestDTO(Long targetUserId,
                                   Long userId,
                                   ReportType reportType,
                                   String reportDetail) {
}
