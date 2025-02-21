package com.spoony.spoony_server.adapter.dto.report;

public record ReportRequestDTO(long postId,
                               long userId,
                               ReportType reportType,
                               String reportDetail) {
}