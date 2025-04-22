package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.PostSearchHistoryResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.ReviewAmountResponseDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostSearchCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserReviewGetCommand;

public interface PostGetUseCase {
    PostResponseDTO getPostById(PostGetCommand command);
    FeedListResponseDTO getPostsByUserId(UserReviewGetCommand command);
    ReviewAmountResponseDTO getPostAmountByUserId(UserGetCommand command);

    PostSearchHistoryResponseDTO getReviewSearchHistory(UserGetCommand command);

}

