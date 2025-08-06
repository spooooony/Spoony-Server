package com.spoony.spoony_server.adapter.out.persistence.admin;

import com.spoony.spoony_server.adapter.out.persistence.admin.db.AdminEntity;
import com.spoony.spoony_server.adapter.out.persistence.admin.db.AdminRepository;
import com.spoony.spoony_server.application.auth.port.out.AdminPort;
import com.spoony.spoony_server.domain.admin.Admin;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminPersistenceAdapter implements AdminPort {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Admin findByAdminId(Long adminId) {
        return adminRepository.findById(adminId)
                .map(AdminEntity::toDomain)
                .orElseThrow(() -> new AuthException(AuthErrorMessage.ADMIN_NOT_FOUND));
    }

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email)
                .map(AdminEntity::toDomain)
                .orElseThrow(() -> new AuthException(AuthErrorMessage.ADMIN_NOT_FOUND));
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
