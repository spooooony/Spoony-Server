package com.spoony.spoony_server.domain.report.dto.request;


import com.spoony.spoony_server.domain.report.enums.ReportType;


public record ReportRequestDTO(Long postId, Long userId, ReportType reportType, String reportDetail) {
}