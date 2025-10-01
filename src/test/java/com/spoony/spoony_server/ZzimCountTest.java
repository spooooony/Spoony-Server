package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.application.port.command.zzim.ZzimAddCommand;
import com.spoony.spoony_server.application.port.command.zzim.ZzimDeleteCommand;
import com.spoony.spoony_server.application.service.zzim.ZzimPostService;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    void 한유저가_같은글_따닥저장_최종_카운트_1() throws Exception {
        final Long uid = 1L;
        final Long pid = 4L;

        int threads = 2;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger already = new AtomicInteger();
        AtomicInteger other   = new AtomicInteger();

        Runnable work = () -> {
            ready.countDown();
            try { start.await(); } catch (InterruptedException ignored) {}
            try {
                zzimService.addZzimPost(new ZzimAddCommand(uid, pid));
                success.incrementAndGet();
            } catch (BusinessException be) {
                if (be.getErrorMessage() == PostErrorMessage.ALREADY_ZZIM) {
                    already.incrementAndGet();
                } else {
                    other.incrementAndGet();
                }
            } catch (Exception e) {
                other.incrementAndGet();
            } finally {
                done.countDown();
            }
        };

        for (int i = 0; i < threads; i++) es.submit(work);

        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 정확히 한 번만 성공, 한 번은 중복 예외, 그 외 예외 없음
        assertThat(success.get()).isEqualTo(1);
        assertThat(already.get()).isEqualTo(1);
        assertThat(other.get()).isEqualTo(0);

        // zzim_post 최종 1건 존재
        assertThat(zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, pid)).isTrue();

        // post.zzimCount 최종 1
        long count = postRepository.findById(pid).orElseThrow().getZzimCount();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void 같은유저_같은포스트_따닥삭제시_최종값_0() throws Exception {
        final long uid = 1L;
        final long pid = 4L;

        int threads = 2;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        AtomicInteger otherErrors = new AtomicInteger();

        Runnable work = () -> {
            ready.countDown();
            try { start.await(); } catch (InterruptedException ignored) {}
            try {
                zzimService.deleteZzim(new ZzimDeleteCommand(uid, pid));
            } catch (Exception e) {
                otherErrors.incrementAndGet();
            } finally {
                done.countDown();
            }
        };

        for (int i = 0; i < threads; i++) es.submit(work);

        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 예기치 않은 예외 없어야 함
        assertThat(otherErrors.get()).isEqualTo(0);

        // 최종 상태 검증: 레코드 없음 + 카운트 0
        boolean existsAfter = zzimPostRepository.existsByUser_UserIdAndPost_PostId(uid, pid);
        long countAfter = postRepository.findById(pid).orElseThrow().getZzimCount();

        assertThat(existsAfter).isFalse();
        assertThat(countAfter).isZero();
    }

    @Test
    void 두유저가_동시에_광클토글_최종정합성() throws Exception {
        final long pid = 4L;
        final long uidA = 1L;
        final long uidB = 2L;

        int opsPerUser = 50; // 각 유저가 누르는 횟수
        ExecutorService es = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(2);

        class Spammer implements Runnable {
            private final long uid;
            private final Random r = new Random();

            Spammer(long uid) { this.uid = uid; }

            @Override public void run() {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException ignored) {}

                for (int i = 0; i < opsPerUser; i++) {
                    boolean add = r.nextBoolean(); // 랜덤 토글
                    try {
                        if (add) zzimService.addZzimPost(new ZzimAddCommand(uid, pid));
                        else     zzimService.deleteZzim(new ZzimDeleteCommand(uid, pid));
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(r.nextInt(3));
                    } catch (InterruptedException ignored) {}
                }
                done.countDown();
            }
        }

        es.submit(new Spammer(uidA));
        es.submit(new Spammer(uidB));

        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 최종 검증
        long rowCount = zzimPostRepository.countByPost_PostId(pid);
        long denorm   = postRepository.findById(pid).orElseThrow().getZzimCount();

        assertThat(denorm).isEqualTo(rowCount);
        assertThat(denorm).isGreaterThanOrEqualTo(0);
    }

    @Test
    void 한유저_멀티스레드_랜덤토글_최종_행수_카운트_일치() throws Exception {
        final long pid = 4L;
        final long uid = 1L;

        final int threads = 10;
        final int opsPerThread = 10; // 총 100회
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        class Burst implements Runnable {
            private final Random r = new Random();
            @Override public void run() {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException ignored) {}
                for (int i = 0; i < opsPerThread; i++) {
                    boolean add = r.nextBoolean();
                    try {
                        if (add) zzimService.addZzimPost(new ZzimAddCommand(uid, pid));
                        else     zzimService.deleteZzim(new ZzimDeleteCommand(uid, pid));
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(r.nextInt(3));
                    } catch (InterruptedException ignored) {}
                }
                done.countDown();
            }
        }

        for (int i = 0; i < threads; i++) es.submit(new Burst());
        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 실데이터 행수 vs 찜카운트 수
        long rowCount = zzimPostRepository.countByPost_PostId(pid);
        long denorm   = postRepository.findById(pid).orElseThrow().getZzimCount();

        assertThat(denorm).isEqualTo(rowCount);
    }

}
