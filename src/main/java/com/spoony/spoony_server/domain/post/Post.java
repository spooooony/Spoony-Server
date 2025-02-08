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
}
