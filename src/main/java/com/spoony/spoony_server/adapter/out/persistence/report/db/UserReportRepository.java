package com.spoony.spoony_server.adapter.out.persistence.report.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserReportRepository extends JpaRepository<UserReportEntity, Long> {
    @Query("SELECT COUNT(DISTINCT ur.targetUser.userId) FROM UserReportEntity ur")
    int countDistinctTargetUsers();
}
