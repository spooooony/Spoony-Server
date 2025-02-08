package com.spoony.spoony_server.application.service.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.ReportErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.dto.report.ReportRequestDTO;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.dto.report.ReportType;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportCreateUseCase {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        ReportEntity reportEntity = ReportEntity.builder()
                .post(postEntity)
                .user(userEntity)
                .reportType(reportType)
                .reportDetail(command.getReportDetail())
                .build();

        reportRepository.save(reportEntity);
    }
}