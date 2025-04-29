package com.spoony.spoony_server.application.port.command.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSearchCommand {
    private final String query;

}
