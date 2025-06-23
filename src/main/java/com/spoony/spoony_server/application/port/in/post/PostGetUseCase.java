package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.post.response.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.PostResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.ReviewAmountResponseDTO;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserReviewGetCommand;

public interface PostGetUseCase {
    PostResponseDTO getPostById(PostGetCommand command);
    FeedListResponseDTO getPostsByUserId(UserReviewGetCommand command);
}

