package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;

public interface PostGetUseCase {
    PostResponseDTO getPostById(PostGetCommand command);
}
