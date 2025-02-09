package com.spoony.spoony_server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Follow {
    private Long followId;
    private User follower;
    private User following;
}
