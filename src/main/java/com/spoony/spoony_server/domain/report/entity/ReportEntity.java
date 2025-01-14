package com.spoony.spoony_server.domain.report.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;
    private Integer postId;
    private Integer userId;
    private String reportType;
    private String reportDetail;
}
