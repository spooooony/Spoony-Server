package com.spoony.spoony_server.domain.post;

import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Post {
    private Long postId;
    private User user;
    private Place place;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(User user, Place place, String title, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.place = place;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
