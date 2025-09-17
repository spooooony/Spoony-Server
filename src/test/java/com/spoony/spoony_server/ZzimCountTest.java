package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.application.port.command.zzim.ZzimAddCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimDeleteCommand;
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

    private final Long postId = 1L;
    private final List<Long> userIds = Arrays.asList(1L, 2L, 16L);

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

    @Test
    void 서로_다른_세명이_동시에_찜을_취소하면_정확히_3감소() throws Exception {
        for (Long uid : userIds) {
            if (!zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, postId)) {
                zzimService.addZzimPost(new ZzimAddCommand(uid, postId));
            }
        }
        long before = postRepository.findById(postId).orElseThrow().getZzimCount();

        ExecutorService es = Executors.newFixedThreadPool(userIds.size());
        CountDownLatch ready = new CountDownLatch(userIds.size());
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(userIds.size());

        for (Long uid : userIds) {
            es.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException ignored) {}
                zzimService.deleteZzim(new ZzimDeleteCommand(uid, postId));
                done.countDown();
            });
        }

        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 검증: 삭제되었는지 확인
        for (Long uid : userIds) {
            boolean exists = zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, postId);
            assertThat(exists).isFalse();
        }
        long after = postRepository.findById(postId).orElseThrow().getZzimCount();
        assertThat(after).isEqualTo(before - userIds.size());
    }

    @Test
    void 한유저가_저장취소_10번_왕복_최종_카운트_0() throws InterruptedException {
        final Long uid = 1L;
        final Long pid = 4L;

        assertThat(zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, pid)).isFalse();
        assertThat(postRepository.findById(pid).orElseThrow().getZzimCount()).isZero();

        int rounds = 20; // 클릭 횟수 (10쌍)
        for (int i = 0; i < rounds; i++) {
            if (i % 2 == 0) {
                zzimService.addZzimPost(new ZzimAddCommand(uid, pid));
            } else {
                zzimService.deleteZzim(new ZzimDeleteCommand(uid, pid));
            }
            Thread.sleep(5); // 5ms 간격 (최대한 빠르게 누르는 상황을 테스트하기 위함)
        }

        // 중간 검증: 최종 상태는 0
        boolean existsAfterToggle = zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, pid);
        long countAfterToggle = postRepository.findById(pid).orElseThrow().getZzimCount();

        assertThat(existsAfterToggle).isFalse();
        assertThat(countAfterToggle).isZero();

        // 마지막으로 한 번 저장
        zzimService.addZzimPost(new ZzimAddCommand(uid, pid));

        // 최종 검증: 레코드 존재, 카운트 +1
        boolean existsFinal = zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, pid);
        long countFinal = postRepository.findById(pid).orElseThrow().getZzimCount();

        assertThat(existsFinal).isTrue();
        assertThat(countFinal).isEqualTo(1);
    }
}
