package com.spoony.spoony_server.application.port.in.feed;

import com.spoony.spoony_server.adapter.dto.post.response.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.FilteredFeedResponseListDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;

public interface FeedGetUseCase {
    FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command);
    FilteredFeedResponseListDTO getFilteredFeed(FeedFilterCommand command);
}


