package com.spoony.spoony_server.application.port.in.feed;

import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FilteredFeedResponseListDTO;
import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedFilterCommand;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostSearchCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;

public interface FeedGetUseCase {
    FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command);
    //FeedListResponseDTO getAllPosts();

    FilteredFeedResponseListDTO getFilteredFeed(FeedFilterCommand command);


}


