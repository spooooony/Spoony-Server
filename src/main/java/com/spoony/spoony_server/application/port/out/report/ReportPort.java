package com.spoony.spoony_server.application.port.out.report;

import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface ReportPort {

    void saveReport(Report report);

}


