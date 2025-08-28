package com.spoony.spoony_server.global.exception.alert;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
public class DiscordWebhookClient {

    @Value("${alert.discord.enabled:true}")
    private boolean enabled;

    @Value("${alert.discord.webhook-url:}")
    private String webhookUrl;

    private final WebClient webClient = WebClient.builder().build();

    public void send(String json) {
        if (!enabled || webhookUrl == null || webhookUrl.isBlank()) return;

        webClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .subscribe(); // 비동기 전송
    }
}