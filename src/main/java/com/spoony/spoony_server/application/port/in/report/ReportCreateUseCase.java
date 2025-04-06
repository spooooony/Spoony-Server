package com.spoony.spoony_server.application.port.in.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;

public interface ReportCreateUseCase {
    void createReport(ReportCreateCommand command);
    void createUserReport(UserReportCreateCommand command);

}
