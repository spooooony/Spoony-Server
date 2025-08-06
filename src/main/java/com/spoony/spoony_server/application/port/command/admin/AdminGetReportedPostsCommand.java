package com.spoony.spoony_server.application.port.command.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminGetReportedPostsCommand {
    private final Long adminId;
    private final int page;
    private final int size;
}