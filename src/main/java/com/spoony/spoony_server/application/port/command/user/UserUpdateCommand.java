package com.spoony.spoony_server.application.port.command.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UserUpdateCommand {
    private final Long userId;
    private final String userName;
    private final Long regionId;
    private final String introduction;
    private final LocalDateTime birth;
}
