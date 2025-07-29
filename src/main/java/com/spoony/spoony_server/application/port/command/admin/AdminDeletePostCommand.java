package com.spoony.spoony_server.application.port.command.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminDeletePostCommand {
    private final Long postId;
}