package com.spoony.spoony_server.adapter.out.persistence.report.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {


    @Query("SELECT DISTINCT r.post.postId FROM ReportEntity r WHERE r.user.userId = :userId")
    List<Long> findReportedPostIdsByUserId(@Param("userId") Long userId);

}
