package com.spoony.spoony_server.adapter.dto.report.request;

import com.spoony.spoony_server.domain.report.ReportType;
import jakarta.validation.constraints.NotNull;

public record ReportRequestDTO(@NotNull(message = "게시물 ID는 필수 값입니다.") long postId,
                               @NotNull(message = "신고 타입은 필수 값입니다.") ReportType reportType,
                               @NotNull(message = "신고 내용은 필수 값입니다.") String reportDetail) {
}