package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.ScoopPostRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceRepository;
import com.spoony.spoony_server.application.port.command.post.PostScoopPostCommand;
import com.spoony.spoony_server.application.service.post.PostService;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ScoopPostTest {

    @Autowired
    private PostService postService;
    @Autowired
    private SpoonBalanceRepository spoonBalanceRepository;
    @Autowired
    private ScoopPostRepository scoopPostRepository;

    private final Long userId = 2L;
    private final Long postId = 4L;

    @Test
    void 같은_유저가_같은_포스트_따닥시_1회만_성공하고_잔액은_1감소() throws Exception {
        PostScoopPostCommand cmd = new PostScoopPostCommand(userId, postId);

        int threads = 2;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger already = new AtomicInteger();
        AtomicInteger other   = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException ignored) {}
                try {
                    postService.scoopPost(cmd);
                    success.incrementAndGet();
                } catch (BusinessException be) {
                    String m = String.valueOf(be.getErrorMessage());
                    if (m.contains("ALREADY_SCOOPED")) already.incrementAndGet();
                    else other.incrementAndGet();
                } catch (Exception e) {
                    other.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        es.shutdown();

        // 1) 성공 1, ALREADY_SCOOPED 1
        assertThat(success.get()).isEqualTo(1);
        assertThat(already.get()).isEqualTo(1);
        assertThat(other.get()).isEqualTo(0);

        // 2) scoop_post 최종 1건
        assertThat(scoopPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId)).isTrue();

        // 3) 잔액 2 -> 1 (insert 성공한 1건만 차감되어야 함)
        Optional<SpoonBalanceEntity> balOpt = spoonBalanceRepository.findByUser_UserId(userId);
        assertThat(balOpt).isPresent();
        assertThat(balOpt.get().getAmount()).isEqualTo(1L);
    }

    @Test
    void 잔액이0이면_NOT_ENOUGH_SPOONS_예외발생_그리고_행_남지않음() {
        // when & then
        assertThatThrownBy(() -> postService.scoopPost(new PostScoopPostCommand(userId, postId)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(String.valueOf(be.getErrorMessage()))
                            .contains(SpoonErrorMessage.NOT_ENOUGH_SPOONS.name());
                });

        // 보상 로직으로 인해 scoop_post에 행이 남아있지 않아야 함
        boolean exists = scoopPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
        assertThat(exists).isFalse();

        // 잔액은 그대로 0
        long amount = spoonBalanceRepository.findByUser_UserId(userId)
                .orElseThrow().getAmount();
        assertThat(amount).isZero();
    }
}
