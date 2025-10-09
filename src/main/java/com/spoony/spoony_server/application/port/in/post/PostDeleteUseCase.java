package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.command.post.PostDeleteCommand;
import com.spoony.spoony_server.application.port.command.post.PostPhotoDeleteCommand;

public interface PostDeleteUseCase {
    void deletePost(PostDeleteCommand command);
    //void deletePhotos(PostPhotoDeleteCommand command);
}
