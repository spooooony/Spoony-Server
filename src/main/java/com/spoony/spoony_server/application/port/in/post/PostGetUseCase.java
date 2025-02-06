package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.dto.post.PostResponseDTO;

public interface PostGetUseCase {
    PostResponseDTO getPostById(Long postId, Long userId);
}
