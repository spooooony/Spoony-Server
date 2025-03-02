package com.spoony.spoony_server.adapter.in.web.report;

import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.report.ReportRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "사용자 신고 API", description = "특정 게시물과 그 작성자를 신고하는 API")
    public ResponseEntity<ResponseDTO<Void>> createReport(
            @RequestBody ReportRequestDTO reportRequestDTO) {
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
