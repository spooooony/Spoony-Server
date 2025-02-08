package com.spoony.spoony_server.application.port.command.zzim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZzimAddCommand {
    private final Long userId;
    private final Long postId;
}
