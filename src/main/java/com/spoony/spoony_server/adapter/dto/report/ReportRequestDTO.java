package com.spoony.spoony_server.adapter.dto.report;


public record ReportRequestDTO(Long postId,
                               Long userId,
                               ReportType reportType,
                               String reportDetail) {
}