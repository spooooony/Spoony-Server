package com.spoony.spoony_server.application.auth.port.in;

import com.spoony.spoony_server.adapter.auth.dto.request.AdminLoginRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.AdminLoginResponseDTO;

public interface AdminLoginUseCase {
    AdminLoginResponseDTO login(AdminLoginRequestDTO adminLoginRequestDTO);
}
