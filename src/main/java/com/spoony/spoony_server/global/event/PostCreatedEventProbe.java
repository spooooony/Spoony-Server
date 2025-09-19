package com.spoony.spoony_server.global.event;

import com.spoony.spoony_server.application.event.PostCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostCreatedEventProbe {

    private final AtomicInteger counter = new AtomicInteger(0);

    @EventListener
    public void on(PostCreatedEvent e) {
        // AfterCommitListener가 커밋 후 재발행한 PostCreatedEvent를 집계
        counter.incrementAndGet();
    }

    public int count() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}
