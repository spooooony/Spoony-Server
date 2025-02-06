package com.spoony.spoony_server.adapter.out.persistence.report.adapter;

import com.spoony.spoony_server.adapter.out.persistence.report.jpa.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportPersistenceAdapter {

    private final ReportRepository reportRepository;

}
