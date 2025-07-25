package com.spoony.spoony_server.application.port.in.admin;

import com.spoony.spoony_server.adapter.dto.admin.response.ReportedUserListResponseDTO;
import com.spoony.spoony_server.application.port.command.admin.AdminDeleteUserCommand;
import com.spoony.spoony_server.application.port.command.admin.AdminGetReportedUsersCommand;

public interface AdminUserUseCase {
    void deleteUser(AdminDeleteUserCommand adminDeleteUserCommand);

    ReportedUserListResponseDTO getReportedUsers(AdminGetReportedUsersCommand command);
}
