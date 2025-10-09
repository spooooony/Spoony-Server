package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.feed.event.PostCreatedEventListener;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(PostCreatedEventListener.class)
public class AsyncThreadLoggingTest {

    @MockBean private UserRepository userRepository;
    @MockBean private PostRepository postRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void printThreadInfo_whenAsyncEventListenerTriggered() throws InterruptedException {
        // given
        Long postId = 1L;
        List<Long> followers = List.of(1L, 2L, 3L);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(Mockito.mock(PostEntity.class)));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(Mockito.mock(UserEntity.class)));

        // when
        eventPublisher.publishEvent(new PostCreatedEvent(this, followers, postId));

        // then
        Thread.sleep(500); // async 대기
    }
}

