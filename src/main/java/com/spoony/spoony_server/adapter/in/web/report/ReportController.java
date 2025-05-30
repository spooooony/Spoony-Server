package com.spoony.spoony_server.adapter.in.web.report;

import com.spoony.spoony_server.adapter.dto.report.request.ReportUserRequestDTO;
import com.spoony.spoony_server.application.port.command.report.ReportCreateCommand;
import com.spoony.spoony_server.application.port.command.report.UserReportCreateCommand;
import com.spoony.spoony_server.application.port.in.report.ReportCreateUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.report.request.ReportRequestDTO;
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
            @UserId Long userId,
            @RequestBody ReportUserRequestDTO reportUserRequestDTO) {
        UserReportCreateCommand command = new UserReportCreateCommand(
                reportUserRequestDTO.targetUserId(),
                userId,
                reportUserRequestDTO.userReportType(),
                reportUserRequestDTO.reportDetail()
        );

        // 신고된 유저가 작성한 게시물이 내가 찜한 목록에 있는지 확인하고 삭제
        reportCreateUseCase.createUserReport(command);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @PostMapping("/post")
    @Operation(summary = "게시물 신고 API", description = "특정 게시물을 신고하는 API")
    public ResponseEntity<ResponseDTO<Void>> createPostReport(
            @UserId Long userId,
            @RequestBody ReportRequestDTO reportRequestDTO) {
        ReportCreateCommand command = new ReportCreateCommand(
                reportRequestDTO.postId(),
                userId,
                reportRequestDTO.reportType(),
                reportRequestDTO.reportDetail()
        );
        reportCreateUseCase.createReport(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
