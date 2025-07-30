package com.spoony.spoony_server.adapter.auth.in.web;

import com.spoony.spoony_server.adapter.auth.dto.request.AdminLoginRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.AdminLoginResponseDTO;
import com.spoony.spoony_server.application.auth.port.in.AdminLoginUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
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
@RequestMapping("/api/v1/admin")
public class AdminAuthController {

    private final AdminLoginUseCase adminLoginUseCase;

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "관리자로 로그인합니다.")
    public ResponseEntity<ResponseDTO<AdminLoginResponseDTO>> adminLogin(
            @RequestBody final AdminLoginRequestDTO adminLoginRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(adminLoginUseCase.login(adminLoginRequestDTO)));
    }
}
