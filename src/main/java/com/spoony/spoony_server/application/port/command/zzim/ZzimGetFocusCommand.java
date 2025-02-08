package com.spoony.spoony_server.application.port.command.zzim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZzimGetFocusCommand {
    private final long userId;
    private final long placeId;
}
