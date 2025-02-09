package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;
import com.spoony.spoony_server.application.port.command.post.PostScoopPostCommand;

public interface PostScoopPostUseCase {
    void scoopPost(PostScoopPostCommand command);
}
