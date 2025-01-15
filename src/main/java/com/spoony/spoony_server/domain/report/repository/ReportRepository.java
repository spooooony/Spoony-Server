package com.spoony.spoony_server.domain.report.repository;

import com.spoony.spoony_server.domain.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {


}
