package com.spoony.spoony_server.application.port.out.report;

import com.spoony.spoony_server.domain.report.Report;

public interface ReportPort {
    void saveReport(Report report);
}
