package com.spoony.spoony_server.adapter.dto.report.request;

import com.spoony.spoony_server.domain.report.UserReportType;
import jakarta.validation.constraints.NotNull;

public record ReportUserRequestDTO(@NotNull(message = "신고 대상 사용자 ID는 필수 값입니다.") long targetUserId,
                                   @NotNull(message = "신고 타입은 필수 값입니다.") UserReportType userReportType,
                                   @NotNull(message = "신고 내용은 필수 값입니다.") String reportDetail) {
}
