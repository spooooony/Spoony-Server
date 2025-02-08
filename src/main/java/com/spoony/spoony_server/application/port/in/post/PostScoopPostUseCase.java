package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;

public interface PostScoopPostUseCase {
    void scoopPost(ScoopPostRequestDTO scoopPostRequestDTO);
}
