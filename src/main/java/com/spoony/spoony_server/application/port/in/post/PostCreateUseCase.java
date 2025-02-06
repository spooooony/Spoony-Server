package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.dto.post.PostCreateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostCreateUseCase {
    List<String> savePostImages(List<MultipartFile> photos) throws IOException;
    void createPost(PostCreateDTO postCreateDTO);
}
