package com.spoony.spoony_server.domain.report;

import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Report {
    private Long reportId;
    private ReportType reportType;
    private String reportDetail;
    private Post post;
    private User user;
    private LocalDateTime createdAt;


    public Report(ReportType reportType,String reportDetail, Post post,User user) {
        this.reportType = reportType;
        this.reportDetail = reportDetail;
        this.post = post;
        this.user = user;
    }
}
