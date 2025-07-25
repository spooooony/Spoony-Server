package com.spoony.spoony_server.application.service.admin;

import com.spoony.spoony_server.adapter.dto.admin.response.*;
import com.spoony.spoony_server.application.port.command.admin.*;
import com.spoony.spoony_server.application.port.in.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements AdminPostUseCase, AdminUserUseCase {

}