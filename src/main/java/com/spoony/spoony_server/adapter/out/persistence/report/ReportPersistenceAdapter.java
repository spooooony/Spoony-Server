package com.spoony.spoony_server.adapter.out.persistence.report;

import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportPersistenceAdapter implements ReportPort {

    private  final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public void saveReport(Report report) {
        UserEntity userEntity = userRepository.findById(report.getUser().getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        PostEntity postEntity = postRepository.findById(report.getPost().getPostId()).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        ReportEntity reportEntity = ReportEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .reportType(report.getReportType())
                .reportDetail(report.getReportDetail())
                .build();
        reportRepository.save(reportEntity);
    }
}
