package com.spoony.spoony_server.domain.report.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.message.BusinessErrorMessage;
import com.spoony.spoony_server.domain.report.dto.request.ReportRequestDTO;
import com.spoony.spoony_server.domain.report.dto.response.ReportResponseDTO;
import com.spoony.spoony_server.domain.report.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/report")
public class ReportController {

    public final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ReportResponseDTO>> createReport(@RequestBody ReportRequestDTO reportRequestDTO) {


        ReportRequestDTO updatedRequestDTO = new ReportRequestDTO(
                reportRequestDTO.postId(),
                reportRequestDTO.userId(),
                reportRequestDTO.reportType(),
                reportRequestDTO.reportDetail()
        );

        try {
            ReportResponseDTO responseDTO = reportService.createReport(updatedRequestDTO);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseDTO.success(responseDTO));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.fail(BusinessErrorMessage.BAD_REQUEST));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.fail(BusinessErrorMessage.INTERNAL_SERVER_ERROR));
        }
    }
}