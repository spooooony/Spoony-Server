package com.spoony.spoony_server.application.port.command.zzim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZzimDeleteCommand {
    private final long userId;
    private final long postId;
}
