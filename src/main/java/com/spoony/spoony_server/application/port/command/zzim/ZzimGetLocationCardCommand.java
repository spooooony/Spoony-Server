package com.spoony.spoony_server.application.port.command.zzim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZzimGetLocationCardCommand {
    private final long userId;
    private final long locationId;
}
