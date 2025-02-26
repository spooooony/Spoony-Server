package com.spoony.spoony_server.adapter.out.persistence.report.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.domain.report.ReportType;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    private ReportType reportType;
    private String reportDetail;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;



    @Builder
    public ReportEntity(Long reportId, PostEntity post, UserEntity user, ReportType reportType, String reportDetail) {
        this.reportId = reportId;
        this.post = post;
        this.user = user;
        this.reportType = reportType;
        this.reportDetail = reportDetail;
    }
}
