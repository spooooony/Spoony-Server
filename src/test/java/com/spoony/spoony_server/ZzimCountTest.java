package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.application.port.command.zzim.ZzimAddCommand;
import com.spoony.spoony_server.application.service.zzim.ZzimPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ZzimCountTest {

    @Autowired private ZzimPostService zzimService;
    @Autowired private PostRepository postRepository;
    @Autowired private ZzimPostRepository zzimPostRepository;

    private final Long postId = 28L;
    private final List<Long> userIds = Arrays.asList(33L, 34L, 35L);

    @Test
    void 서로_다른_세명이_동시에_찜하면_정확히_3증가() throws Exception {
        long base = postRepository.findById(postId).orElseThrow().getZzimCount();

        ExecutorService es = Executors.newFixedThreadPool(userIds.size());
        CountDownLatch ready = new CountDownLatch(userIds.size());
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(userIds.size());

        for (Long uid : userIds) {
            es.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException ignored) {}
                zzimService.addZzimPost(new ZzimAddCommand(uid, postId));
                done.countDown();
            });
        }
        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 검증: 각 유저의 찜이 존재, 카운트 +3
        for (Long uid : userIds) {
            boolean exists = zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, postId);
            assertThat(exists).isTrue();
        }
        long after = postRepository.findById(postId).orElseThrow().getZzimCount();
        assertThat(after).isEqualTo(base + 3);
    }
}
