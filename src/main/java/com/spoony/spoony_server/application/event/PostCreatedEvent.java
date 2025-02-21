package com.spoony.spoony_server.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class PostCreatedEvent extends ApplicationEvent {
    private final List<Long> followerIds;
    private final Long postId;

    public PostCreatedEvent(Object source, List<Long> followerIds, Long postId) {
        super(source);
        this.followerIds = followerIds;
        this.postId = postId;
    }
}
