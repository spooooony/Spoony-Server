package com.spoony.spoony_server.application.port.command.report;

import com.spoony.spoony_server.adapter.dto.report.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportCreateCommand {
    private final long postId;
    private final long userId;
    private final ReportType reportType;
    private final String reportDetail;
}
