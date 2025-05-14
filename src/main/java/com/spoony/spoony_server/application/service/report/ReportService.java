package com.spoony.spoony_server.application.service.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;
import com.spoony.spoony_server.domain.report.UserReportType;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.ReportErrorMessage;
import com.spoony.spoony_server.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService implements ReportCreateUseCase {
    private final ReportPort reportPort;
    private final PostPort postPort;
    private final UserPort userPort;
    private final FeedPort feedPort;

    public void createReport(ReportCreateCommand command) {
        if (command.getReportDetail().trim().isEmpty()) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_MISSING);
        }
        if (command.getReportDetail().length() > 300) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_TOO_LONG);
        }

        ReportType reportType = command.getReportType();

        if (reportType == null) {
            reportType = ReportType.PROMOTIONAL_CONTENT;
        }

        Long postId = command.getPostId();
        Long userId = command.getUserId();

        Post post = postPort.findPostById(postId);
        User user = userPort.findUserById(userId);

        //Feed테이블 -> 신고자(user_id) , 게시물(post_id) 삭제 (팔로잉 기반 피드조회시)
        feedPort.deleteFeedByUserIdAndPostId(userId,postId);

        Report report = new Report(reportType,command.getReportDetail(),post,user);
        reportPort.saveReport(report);
    }

    @Override
    public void createUserReport(UserReportCreateCommand command) {
        if (command.getReportDetail().trim().isEmpty()){
            throw  new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_MISSING);
        }
        if (command.getReportDetail().length()>300){
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_TOO_LONG);
        }
        UserReportType userReportType = command.getUserReportType();
        if (userReportType == null){
            userReportType = UserReportType.PROMOTIONAL_CONTENT;
        }
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        User user = userPort.findUserById(userId);
        User targetUser = userPort.findUserById(targetUserId);

        UserReport userReport = new UserReport(userReportType, command.getReportDetail(),user,targetUser);
        reportPort.saveUserReport(userReport);
    }
}
