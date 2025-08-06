package com.spoony.spoony_server.domain.report;


import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserReport {
    private Long userReportId;
    private UserReportType userReportType;
    private String userReportDetail;
    private User reporter;
    private User targetUser;
    private LocalDateTime createdAt;

    public UserReport(UserReportType userReportType,String userReportDetail, User reporter,User targetUser) {
        this.userReportType = userReportType;
        this.userReportDetail = userReportDetail;
        this.reporter  = reporter;
        this.targetUser = targetUser;
    }
}
