package com.spoony.spoony_server.domain.feed;

import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Feed {
    private Long feedId;
    private User user;
    private User author;
    private Post post;
}
