package com.spoony.spoony_server.domain.report.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.report.dto.request.ReportRequestDTO;
import com.spoony.spoony_server.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    public final ReportService reportService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> createReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        reportService.createReport(reportRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
