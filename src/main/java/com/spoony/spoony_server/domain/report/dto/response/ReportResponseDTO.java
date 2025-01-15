package com.spoony.spoony_server.domain.report.dto.response;


import com.spoony.spoony_server.domain.report.enums.ReportType;


public record ReportResponseDTO(Integer reportId, Integer userId, ReportType reportType, Integer postId) {
}


