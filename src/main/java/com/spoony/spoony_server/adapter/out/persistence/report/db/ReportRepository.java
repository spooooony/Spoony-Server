package com.spoony.spoony_server.adapter.out.persistence.report.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {


    @Query("SELECT DISTINCT r.post.postId FROM Report r WHERE r.user.userId = :userId")
    List<Long> findReportedPostIdsByUserId(@Param("userId") Long userId); //@Param("userId") => JPQL 쿼리의 파라미터 이름(:userId)과 자바 메서드 인자 (Long userId)를 연결해주는 역할
}
