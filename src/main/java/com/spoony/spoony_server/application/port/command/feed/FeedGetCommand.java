package com.spoony.spoony_server.application.port.command.feed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedGetCommand {
    private final Long userId;
    private final Long categoryId;
    private final String locationQuery;
    private final String sortBy;
}
