package com.spoony.spoony_server.application.port.command.place;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceGetCommand {
    private final String query;
    private final int display;
}

