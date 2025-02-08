package com.spoony.spoony_server.application.port.command.post;

import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostScoopPostCommand {
    private final ScoopPostRequestDTO scoopPostRequestDTO;
}
