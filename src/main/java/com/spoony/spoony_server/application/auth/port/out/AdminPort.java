package com.spoony.spoony_server.application.auth.port.out;

import com.spoony.spoony_server.domain.admin.Admin;

public interface AdminPort {
    Admin findByEmail(String email);
    Admin findByAdminId(Long adminId);
    boolean checkPassword(String rawPassword, String encodedPassword);
}
