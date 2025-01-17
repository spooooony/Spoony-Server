package com.spoony.spoony_server.domain.report.service;


import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.ReportErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.report.dto.request.ReportRequestDTO;
import com.spoony.spoony_server.domain.report.entity.ReportEntity;
import com.spoony.spoony_server.domain.report.enums.ReportType;
import com.spoony.spoony_server.domain.report.repository.ReportRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResponseDTO<Void>> createReport(ReportRequestDTO reportRequest) {

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
                .orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));
        ReportEntity reportEntity = ReportEntity.builder()
                .post(postEntity)
                .user(userEntity)
                .reportType(reportType)
                .reportDetail(reportRequest.reportDetail())
                .build();

        ReportEntity savedEntity = reportRepository.save(reportEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}