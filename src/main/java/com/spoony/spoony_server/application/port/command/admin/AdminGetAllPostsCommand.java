package com.spoony.spoony_server.application.port.command.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminGetAllPostsCommand {
    private final int page;
    private final int size;
    private final String status;
}