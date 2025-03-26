package com.spoony.spoony_server.application.port.command.spoon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SpoonDrawWeeklyCommand {
    private final Long userId;
}
