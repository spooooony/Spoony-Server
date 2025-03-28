package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.command.post.PostDeleteCommand;

public interface PostDeleteUseCase {
    void deletePost(PostDeleteCommand command);
    void deletePhotos(PostDeleteCommand command);
}
