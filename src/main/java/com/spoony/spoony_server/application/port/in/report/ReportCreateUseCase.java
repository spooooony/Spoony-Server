package com.spoony.spoony_server.application.port.in.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;

public interface ReportCreateUseCase {
    void createReport(ReportCreateCommand command);
}
