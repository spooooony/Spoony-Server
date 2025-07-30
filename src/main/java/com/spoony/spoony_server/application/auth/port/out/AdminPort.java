package com.spoony.spoony_server.application.auth.port.out;

import com.spoony.spoony_server.domain.admin.Admin;

public interface AdminPort {
    Admin findByEmail(String email);
    boolean checkPassword(String rawPassword, String encodedPassword);
}
