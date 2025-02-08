package com.spoony.spoony_server.adapter.out.persistence.report.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}
