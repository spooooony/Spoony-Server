package com.spoony.spoony_server.application.port.command.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostScoopPostCommand {
    private final long userId;
    private final long postId;
}
