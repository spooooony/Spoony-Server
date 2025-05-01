package com.spoony.spoony_server.application.port.command.feed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowingUserFeedGetCommand {
    private final Long userId;

}
