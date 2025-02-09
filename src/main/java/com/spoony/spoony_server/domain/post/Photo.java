package com.spoony.spoony_server.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Photo {
    private Long photoId;
    private Post post;
    private String photoUrl;

    public Photo(Post post, String photoUrl) {
        this.post = post;
        this.photoUrl = photoUrl;
    }
}
