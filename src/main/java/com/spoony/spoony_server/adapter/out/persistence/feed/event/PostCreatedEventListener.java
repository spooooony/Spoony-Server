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

import java.util.List;
import java.util.concurrent.Executors;

@Adapter
@RequiredArgsConstructor
public class PostCreatedEventListener {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FeedRepository feedRepository;

    @EventListener
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        List<Long> followerIds = event.getFollowerIds();
        Long postId = event.getPostId();

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow();

        int batchSize = 10_000;

        var executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < followerIds.size(); i += batchSize) {
            int fromIndex = i;
            int toIndex = Math.min(i + batchSize, followerIds.size());

            executor.submit(() -> {
                List<FeedEntity> feedList = followerIds.subList(fromIndex, toIndex).stream()
                        .map(followerId -> {
                            UserEntity userEntity = userRepository.findById(followerId)
                                    .orElseThrow();
                            return new FeedEntity(userEntity, postEntity);
                        })
                        .toList();

                feedRepository.saveAll(feedList);
            });
        }
        executor.shutdown();
    }
}
