package com.spoony.spoony_server.application.port.command.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class UserReviewGetCommand {
    private final Long userId;
    private final Boolean isLocalReview;
}

