package com.spoony.spoony_server.adapter.out.persistence.report;

import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.*;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ScoopPostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.post.CategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PlaceErrorMessage;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor

//public class PostPersistenceAdapter implements
//        PostPort,
//        PostCategoryPort,
//        CategoryPort {

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


