package com.spoony.spoony_server.application.port.dto.report;


public record ReportRequestDTO(Long postId,
                               Long userId,
                               ReportType reportType,
                               String reportDetail) {
}