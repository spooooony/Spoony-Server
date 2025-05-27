package com.spoony.spoony_server.application.port.command.zzim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZzimGetCardCommand {
    private final long userId;
    private final long categoryId;
}
