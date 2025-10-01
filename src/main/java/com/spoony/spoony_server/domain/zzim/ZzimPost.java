package com.spoony.spoony_server.domain.zzim;

import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Table(
    uniqueConstraints = {
            @UniqueConstraint(name = "uq_zzim_user_post", columnNames = {"user_id", "post_id"})
    }
)
public class ZzimPost {
    private Long zzimId;
    private User user;
    private Post post;

    public ZzimPost(User user,Post post) {
        this.user = user;
        this.post = post;
    }
}
