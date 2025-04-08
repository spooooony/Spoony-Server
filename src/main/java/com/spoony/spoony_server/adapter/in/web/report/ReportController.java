package com.spoony.spoony_server.adapter.in.web.report;

import com.spoony.spoony_server.adapter.dto.report.ReportUserRequestDTO;
import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;
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


    @PostMapping("/user")
    @Operation(summary = "사용자 신고 API", description = "사용자 마이페이지를 신고하는 API")
    public ResponseEntity<ResponseDTO<Void>> createUserReport(
            @RequestBody ReportUserRequestDTO reportUserRequestDTO) {
        UserReportCreateCommand command = new UserReportCreateCommand(
                reportUserRequestDTO.targetUserId(),
                reportUserRequestDTO.userId(),
                reportUserRequestDTO.userReportType(),
                reportUserRequestDTO.reportDetail()
        );
        reportCreateUseCase.createUserReport(command);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
    @PostMapping("/post")
    @Operation(summary = "게시물 신고 API", description = "특정 게시물을 신고하는 API")

    public ResponseEntity<ResponseDTO<Void>> createPostReport(
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
