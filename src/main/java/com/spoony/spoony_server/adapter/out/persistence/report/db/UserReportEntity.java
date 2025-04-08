package com.spoony.spoony_server.adapter.out.persistence.report.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.report.ReportType;
import com.spoony.spoony_server.domain.report.UserReportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_report")
public class UserReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userReportId;
    private UserReportType userReportType;
    private String userReportDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private UserEntity targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id")
    private UserEntity reporter;


    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserReportEntity(Long userReportId, UserEntity targetUser, UserEntity reporter, UserReportType userReportType, String userReportDetail) {
        this.userReportId = userReportId;
        this.targetUser = targetUser;
        this.reporter = reporter;
        this.userReportType = userReportType;
        this.userReportDetail = userReportDetail;
    }
}

