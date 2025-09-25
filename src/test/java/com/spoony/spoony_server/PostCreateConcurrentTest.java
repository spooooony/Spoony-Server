package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.application.port.command.post.PostCreateCommand;
import com.spoony.spoony_server.application.port.in.post.PostCreateUseCase;
import com.spoony.spoony_server.global.event.PostCreatedEventProbe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PostCreateConcurrentTest {

    @Autowired
    private PostCreateUseCase postCreateUseCase;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCreatedEventProbe eventProbe;

    private final Long userId = 1L;
    private final Long categoryId = 1L;

    @AfterEach
    void tearDown() {
        eventProbe.reset();
    }

    @Test
    void 따닥으로_글_두번_등록해도_글1개_이벤트1회() throws Exception {
        String placeName = "test-place";
        double lat = 37.12345;
        double lon = 127.12345;

        // 동일 데이터로 두 번 호출
        PostCreateCommand cmd = new PostCreateCommand(
                userId,
                "동시등록 테스트 " + LocalDateTime.now(),
                4.5,
                "단점 없음",
                placeName,
                "서울시 어딘가",
                "서울시 어딘가 123",
                lat,
                lon,
                categoryId,
                List.of("메뉴1", "메뉴2"),
                List.of("image1", "image2")
        );

        int threads = 2;
        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException ignored) {}
                try {
                    postCreateUseCase.createPost(cmd);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(3, TimeUnit.SECONDS);
        start.countDown();
        done.await(5, TimeUnit.SECONDS);
        es.shutdown();

        long count = postRepository.count();

        // 글이 한개만 생성되어 전체글 3개
        assertThat(count).isEqualTo(3);

        // 이벤트는 1회만 발행
        assertThat(eventProbe.count()).isEqualTo(1);
    }
}
