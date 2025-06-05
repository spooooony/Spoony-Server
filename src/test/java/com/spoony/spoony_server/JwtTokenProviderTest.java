package com.spoony.spoony_server;

import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void generateTokenPair_ShouldReturnValidTokens() {
        Long userId = 116L;

        JwtTokenDTO tokenPair = jwtTokenProvider.generateTokenPair(userId);

        System.out.println("AccessToken: " + tokenPair.accessToken());
        System.out.println("RefreshToken: " + tokenPair.refreshToken());

        assertThat(tokenPair.accessToken()).isNotBlank();
        assertThat(tokenPair.refreshToken()).isNotBlank();
    }
}
