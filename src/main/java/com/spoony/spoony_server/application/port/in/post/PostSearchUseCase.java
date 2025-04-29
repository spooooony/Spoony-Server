package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.post.PostSearchResultListDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSearchResultListDTO;
import com.spoony.spoony_server.application.port.command.post.PostSearchCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserSearchCommand;

public interface PostSearchUseCase {
    PostSearchResultListDTO searchReviewsByQuery(UserGetCommand userGetCommand,PostSearchCommand postSearchCommand);
}


