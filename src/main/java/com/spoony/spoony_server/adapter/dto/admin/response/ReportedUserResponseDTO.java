package com.spoony.spoony_server.adapter.dto.admin.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReportedUserResponseDTO(String userId, String userName, int reportCount, List<ReportDTO> reports) {
    public static ReportedUserResponseDTO of(String userId, String userName, int reportCount, List<ReportDTO> reports) {
        return new ReportedUserResponseDTO(userId, userName, reportCount, reports);
    }

    public record ReportDTO(String id,
                            String reportType,
                            String reportDetail,
                            String reporterName,
                            LocalDateTime createdAt) {
        public static ReportDTO of(String id,
                                   String reportType,
                                   String reportDetail,
                                   String reporterName,
                                   LocalDateTime createdAt) {
            return new ReportDTO(id, reportType, reportDetail, reporterName, createdAt);
        }
    }
}