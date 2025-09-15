package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.auth.out.persistence.AppleRefreshTokenAdapter;
import com.spoony.spoony_server.adapter.out.persistence.user.db.AppleRefreshTokenEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.AppleRefreshTokenRepository;
import com.spoony.spoony_server.application.auth.port.out.AppleRefreshTokenPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({
        AppleRefreshTokenAdapter.class,
        com.spoony.spoony_server.global.auth.encryptor.AppleRefreshTokenEncryptor.KeyLoader.class
})
@TestPropertySource(properties = {
        // 32바이트 AES 테스트용 키(Base64)
        "security.token.key-base64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
public class AppleRefreshTokenUpsertTest {

    @Autowired
    AppleRefreshTokenPort port;

    @Autowired
    AppleRefreshTokenRepository repository;

    @Autowired
    JdbcTemplate jdbc;

    // 암호화 및 복호화가 JPA 단에서 잘 잘동하는지
    @Test
    void upsert_insert_shouldEncryptColumn_andDecryptOnRead() {
        Long userId = 92001L;
        String plain = "token-insert-realdb";

        // when
        port.upsert(userId, plain);
        repository.flush();

        // then
        AppleRefreshTokenEntity loaded = repository.findById(userId).orElseThrow();
        assertThat(loaded.getRefreshToken()).isEqualTo(plain);

        // 실제 DB 컬럼은 암호문이어야 함
        String raw = jdbc.queryForObject(
                "select refresh_token from apple_refresh_token where user_id=?",
                String.class, userId
        );
        assertThat(raw).startsWith("v1:");
        assertThat(raw).doesNotContain(plain);

        // row 1건
        Integer cnt = jdbc.queryForObject(
                "select count(*) from apple_refresh_token where user_id=?",
                Integer.class, userId
        );
        assertThat(cnt).isEqualTo(1);
    }

    // 신규 추가가 잘되는지, 기존에 있는 유저라면 잘 갈아끼워지는지
    @Test
    void upsert_update_shouldOverwrite_withoutDuplicateRow_andChangeCiphertext() {
        Long userId = 92002L;

        // given
        port.upsert(userId, "old-token");
        repository.flush();
        String beforeCipher = jdbc.queryForObject(
                "select refresh_token from apple_refresh_token where user_id=?",
                String.class, userId
        );

        // when
        port.upsert(userId, "new-token");
        repository.flush();

        // then
        AppleRefreshTokenEntity loaded = repository.findById(userId).orElseThrow();
        assertThat(loaded.getRefreshToken()).isEqualTo("new-token");

        // row 1건
        Integer cnt = jdbc.queryForObject(
                "select count(*) from apple_refresh_token where user_id=?",
                Integer.class, userId
        );
        assertThat(cnt).isEqualTo(1);

        // 암호문 변경
        String afterCipher = jdbc.queryForObject(
                "select refresh_token from apple_refresh_token where user_id=?",
                String.class, userId
        );
        assertThat(afterCipher).startsWith("v1:");
        assertThat(afterCipher).isNotEqualTo(beforeCipher);
    }

    // user_id를 이용한 refresh token 조회
    @Test
    void findRefreshTokenByUserId_shouldReturnOptional() {
        Long userId = 92003L;

        // not present
        Optional<String> none = port.findRefreshTokenByUserId(userId);
        assertThat(none).isEmpty();

        // insert
        port.upsert(userId, "found-me");
        Optional<String> some = port.findRefreshTokenByUserId(userId);
        assertThat(some).contains("found-me");
    }
}
