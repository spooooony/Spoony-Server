package com.spoony.spoony_server.adapter.out.persistence.feed.event;

import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import com.spoony.spoony_server.global.annotation.Adapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Adapter
@Async
@RequiredArgsConstructor
public class PostCreatedEventListener {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FeedRepository feedRepository;

    @EventListener
    @Transactional
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        System.out.println("🔥 이벤트 리스너 실행 스레드: " + Thread.currentThread());

        List<Long> followerIds = event.getFollowerIds();
        Long postId = event.getPostId();

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found. id = " + postId));

        UserEntity author = postEntity.getUser();

        for (int i = 0; i < followerIds.size(); i += 10_000) {
            int fromIndex = i;
            int toIndex = Math.min(i + 10_000, followerIds.size());

            List<FeedEntity> feedList = followerIds.subList(fromIndex, toIndex).stream()
                    .map(followerId -> {
                        UserEntity userEntity = userRepository.findById(followerId)
                                .orElseThrow(() -> new IllegalStateException("Follower not found. id = " + followerId));
                        return new FeedEntity(userEntity, postEntity, author);
                    })
                    .toList();

            feedRepository.saveAll(feedList);
        }
    }
}
