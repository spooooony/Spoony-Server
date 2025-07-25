package com.spoony.spoony_server.adapter.out.persistence.report;

import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.report.db.UserReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.UserReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.report.mapper.ReportMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Adapter
@Transactional
@RequiredArgsConstructor
public class ReportPersistenceAdapter implements ReportPort {

    private  final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private  final UserReportRepository userReportRepository;

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

    @Override
    public void saveUserReport(UserReport userReport) {
        UserEntity fromUserEntity = userRepository.findById(userReport.getReporter().getUserId()).orElseThrow(()-> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        UserEntity toUserEntity = userRepository.findById(userReport.getTargetUser().getUserId()).orElseThrow(()-> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        UserReportEntity userReportEntity = UserReportEntity.builder()
                .reporter(fromUserEntity)
                .targetUser(toUserEntity)
                .userReportType(userReport.getUserReportType()).userReportDetail(userReport.getUserReportDetail())
                .build();
        userReportRepository.save(userReportEntity);
    }

    @Override
    public List<Long> findReportedPostIdsByUserId(Long userId) {
        return reportRepository.findReportedPostIdsByUserId(userId);
    }

    @Override
    public Map<Long, List<Report>> findReportsByPostIds(List<Long> postIds) {
        List<ReportEntity> entities = reportRepository.findAllByPost_PostIdIn(postIds);
        return entities.stream()
                .map(ReportMapper::toDomain)
                .collect(Collectors.groupingBy(r -> r.getPost().getPostId()));
    }
}
