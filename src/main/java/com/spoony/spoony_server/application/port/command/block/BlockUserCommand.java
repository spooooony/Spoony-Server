package com.spoony.spoony_server.application.port.command.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockUserCommand {
    private final Long userId;
    private final Long targetUserId;
}

