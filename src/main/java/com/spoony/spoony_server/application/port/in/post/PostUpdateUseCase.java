package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.command.post.PostUpdateCommand;
import com.spoony.spoony_server.domain.post.Post;

public interface PostUpdateUseCase {
    public void updatePost(PostUpdateCommand command);
}
