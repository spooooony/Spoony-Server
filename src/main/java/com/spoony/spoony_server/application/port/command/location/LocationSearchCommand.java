package com.spoony.spoony_server.application.port.command.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocationSearchCommand {
    private final String query;
}
