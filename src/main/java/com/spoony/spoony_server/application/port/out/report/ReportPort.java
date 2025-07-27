package com.spoony.spoony_server.application.port.out.report;

import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;

import java.util.List;
import java.util.Map;

public interface ReportPort {
    void saveReport(Report report);
    void saveUserReport(UserReport userReport);
    List<Long> findReportedPostIdsByUserId(Long userId);
    Map<Long, List<Report>> findReportsByPostIds(List<Long> postIds);
    List<UserReport> findUserReportsWithPagination(int page, int size);
    int countReportedUsers();
}


