package com.spoony.spoony_server.application.service.report;

import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.ReportErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostRepository;
import com.spoony.spoony_server.application.port.dto.report.ReportRequestDTO;
import com.spoony.spoony_server.adapter.out.persistence.report.jpa.ReportEntity;
import com.spoony.spoony_server.application.port.dto.report.ReportType;
import com.spoony.spoony_server.adapter.out.persistence.report.jpa.ReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportCreateUseCase {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void createReport(ReportRequestDTO reportRequest) {
        if (reportRequest.reportDetail().trim().isEmpty()) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_MISSING);
        }
        if (reportRequest.reportDetail().length() > 300) {
            throw new BusinessException(ReportErrorMessage.BAD_REQUEST_CONTENT_TOO_LONG);
        }

        ReportType reportType = reportRequest.reportType();
        if (reportType == null) {
            reportType = ReportType.ADVERTISEMENT;
        }

        Long postId = reportRequest.postId();
        Long userId = reportRequest.userId();

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        ReportEntity reportEntity = ReportEntity.builder()
                .post(postEntity)
                .user(userEntity)
                .reportType(reportType)
                .reportDetail(reportRequest.reportDetail())
                .build();

        reportRepository.save(reportEntity);
    }
}