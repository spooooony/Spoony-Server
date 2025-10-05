package com.spoony.spoony_server.domain.zzim;

import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ZzimPost {
    private Long zzimId;
    private User user;
    private Post post;

    public ZzimPost(User user,Post post) {
        this.user = user;
        this.post = post;
    }
}
