package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.request.AdminLoginRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.AdminLoginResponseDTO;
import com.spoony.spoony_server.application.auth.port.in.AdminLoginUseCase;
import com.spoony.spoony_server.application.auth.port.out.AdminPort;
import com.spoony.spoony_server.domain.admin.Admin;
import com.spoony.spoony_server.global.auth.jwt.AdminJwtTokenProvider;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService implements AdminLoginUseCase {

    private final AdminPort adminPort;
    private final AdminJwtTokenProvider adminJwtTokenProvider;

    @Override
    public AdminLoginResponseDTO login(AdminLoginRequestDTO adminLoginRequestDTO) {
        Admin admin = adminPort.findByEmail(adminLoginRequestDTO.email());
        if(admin == null) {
            return AdminLoginResponseDTO.of(false, null, null, null);
        }

        boolean valid = adminPort.checkPassword(adminLoginRequestDTO.password(), admin.getPassword());
        if (!valid) {
            throw new AuthException(AuthErrorMessage.PASSWORD_NOT_MATCHED);
        }

        String accessToken = adminJwtTokenProvider.generateToken(admin.getAdminId());

        return AdminLoginResponseDTO.of(true, admin.getAdminId(), admin.getEmail(), accessToken);
    }
}
