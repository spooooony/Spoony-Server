package com.spoony.spoony_server.application.port.out.report;

import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;

import java.util.List;

public interface ReportPort {
    void saveReport(Report report);
    void saveUserReport(UserReport userReport);
    List<Long> findReportedPostIdsByUserId(Long userId);

}


