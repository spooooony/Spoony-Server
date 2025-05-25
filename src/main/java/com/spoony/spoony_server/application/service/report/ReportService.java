package com.spoony.spoony_server.application.service.report;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService implements ReportCreateUseCase {
    private final ReportPort reportPort;
    private final PostPort postPort;
    private final UserPort userPort;
    private final FeedPort feedPort;
    private final BlockPort blockPort;
    private final ZzimPostPort zzimPostPort;

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


        blockPort.saveOrUpdateUserBlockRelation(userId, targetUserId, BlockStatus.REPORT);

        // 2. follow 관계 제거 (양방향)
        userPort.deleteFollowRelation(userId, targetUserId);
        userPort.deleteFollowRelation(targetUserId, userId);

        //3. zzimPost 관계 제거(양방향)

        // 신고된 유저의 게시물 목록 조회 (List<Post>)
        List<Post> postsByReportedUsers = postPort.findPostsByUserId(targetUserId);

        //신고자의 게시물 목록 조회
        List<Post> postsByReporter = postPort.findPostsByUserId(userId);

        // Post에서 ID만 추출하여 List<Long>으로 변환
        List<Long> reportedPostIds = postsByReportedUsers.stream()
                .map(post -> post.getPostId())
                .toList();  // List<Long>으로 변환

        List<Long> reporterPostIds = postsByReporter.stream()
                .map(post -> post.getPostId())
                .toList();  // List<Long>으로 변환

        //나의 찜리스트에서 -> 내가 신고한 사람의 게시물 삭제
        reportedPostIds.forEach(postId -> {
            if (zzimPostPort.existsByUserIdAndPostId(userId,postId)){
                Post post = postPort.findPostById(postId);
                //User user = userPort.findUserById(userId);
                zzimPostPort.deleteByUserAndPost(user,post);
            }
        });

        //신고당한 사람의 찜리스트에서 -> 나의 게시물 삭제
        reporterPostIds.forEach(postId -> {
            if (zzimPostPort.existsByUserIdAndPostId(targetUserId,postId)){
                Post post = postPort.findPostById(postId);
                zzimPostPort.deleteByUserAndPost(targetUser,post);
            }
        });

        UserReport userReport = new UserReport(userReportType, command.getReportDetail(),user,targetUser);
        reportPort.saveUserReport(userReport);
    }
}
