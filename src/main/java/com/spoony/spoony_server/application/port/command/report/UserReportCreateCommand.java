package com.spoony.spoony_server.application.port.command.report;

import com.spoony.spoony_server.domain.report.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class UserReportCreateCommand {
    private final long targetUserId;
    private final long userId;
    private final ReportType reportType;
    private final String reportDetail;
}
