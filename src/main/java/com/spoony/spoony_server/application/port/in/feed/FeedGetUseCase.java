package com.spoony.spoony_server.application.port.in.feed;

import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;

public interface FeedGetUseCase {
    FeedListResponseDTO getFeedListByUserId(FeedGetCommand command);

}


