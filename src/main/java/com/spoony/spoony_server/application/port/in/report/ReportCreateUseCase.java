package com.spoony.spoony_server.application.port.in.report;

import com.spoony.spoony_server.application.port.dto.report.ReportRequestDTO;

public interface ReportCreateUseCase {
    void createReport(ReportRequestDTO reportRequest);
}
