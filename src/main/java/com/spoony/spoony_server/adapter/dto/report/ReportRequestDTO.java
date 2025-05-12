package com.spoony.spoony_server.adapter.dto.report;

import com.spoony.spoony_server.domain.report.ReportType;

public record ReportRequestDTO(long postId,
                               ReportType reportType,
                               String reportDetail) {
}