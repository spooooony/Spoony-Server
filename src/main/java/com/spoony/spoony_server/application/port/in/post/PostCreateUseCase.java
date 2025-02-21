package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.command.post.PostCreateCommand;
import com.spoony.spoony_server.application.port.command.post.PostPhotoSaveCommand;

import java.io.IOException;
import java.util.List;

public interface PostCreateUseCase {
    List<String> savePostImages(PostPhotoSaveCommand photoSaveCommand) throws IOException;
    void createPost(PostCreateCommand command);
}
