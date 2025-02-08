package com.spoony.spoony_server.application.port.command.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostGetCommand {
    private final Long postId;
    private final Long userId;
}
