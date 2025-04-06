package com.spoony.spoony_server.application.service.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.ReportErrorMessage;
import com.spoony.spoony_server.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportCreateUseCase {
    private final ReportPort reportPort;
    private final PostPort postPort;
    private final UserPort userPort;

    public void createReport(ReportCreateCommand command) {
        if (command.getReportDetail().trim().isEmpty()) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_MISSING);
        }
        if (command.getReportDetail().length() > 300) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_TOO_LONG);
        }

        ReportType reportType = command.getReportType();

        if (reportType == null) {
            reportType = ReportType.ADVERTISEMENT;
        }

        Long postId = command.getPostId();
        Long userId = command.getUserId();

        Post post = postPort.findPostById(postId);
        User user = userPort.findUserById(userId);

        Report report = new Report(reportType,command.getReportDetail(),post,user);
        reportPort.saveReport(report);
    }

    @Override
    public void createUserReport(UserReportCreateCommand command) {

    }
}
