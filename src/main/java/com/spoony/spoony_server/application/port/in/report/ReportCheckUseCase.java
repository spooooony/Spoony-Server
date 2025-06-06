package com.spoony.spoony_server.application.port.in.report;

import com.spoony.spoony_server.application.port.command.user.BlockCheckCommand;

public interface ReportCheckUseCase {
    boolean isBlockedByPostReporting(BlockCheckCommand command);
}
