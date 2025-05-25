package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.adapter.dto.post.response.PostSearchResultListDTO;
import com.spoony.spoony_server.application.port.command.post.PostSearchCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;

public interface PostSearchUseCase {
    PostSearchResultListDTO searchReviewsByQuery(UserGetCommand userGetCommand,PostSearchCommand postSearchCommand);
}


