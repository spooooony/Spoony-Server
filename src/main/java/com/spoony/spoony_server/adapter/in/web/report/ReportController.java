package com.spoony.spoony_server.adapter.in.web.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.report.ReportRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    public final ReportCreateUseCase reportCreateUseCase;
    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> createReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        ReportCreateCommand command = new ReportCreateCommand(
                reportRequestDTO.postId(),
                reportRequestDTO.userId(),
                reportRequestDTO.reportType(),
                reportRequestDTO.reportDetail()
        );
        reportCreateUseCase.createReport(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
