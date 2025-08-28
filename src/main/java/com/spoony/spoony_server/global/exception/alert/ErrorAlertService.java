package com.spoony.spoony_server.global.exception.alert;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorAlertService {

    private final DiscordWebhookClient discord;

    @Value("${alert.env:local}")   private String env;
    @Value("${alert.service:app}") private String service;
    @Value("${alert.dedup-window-ms:60000}") private long dedupWindowMs;

    // (status + path + message) 기준 중복 억제
    private final Map<String, Long> dedupCache = new ConcurrentHashMap<>();

    /**
     * DefaultErrorMessage 기반(주로 핸들링된 예외) 알림.
     * - 기본 정책: 5xx만 전송 (4xx는 노이즈 가능)
     */
    public void notifyIfNecessary(DefaultErrorMessage msg,
                                  Throwable ex,
                                  HttpServletRequest req) {

        HttpStatus status = msg.getHttpStatus();
        if (!status.is5xxServerError()) return; // 5xx만

        String path = safe(req.getRequestURI());
        String method = safe(req.getMethod());
        String message = safe(msg.getMessage());

        String dedupKey = status.value() + "|" + path + "|" + trim(message, 60);
        if (isDuplicated(dedupKey)) return;

        String ua = maskUA(req.getHeader("User-Agent"));
        String ip = maskIP(req.getRemoteAddr());
        String query = maskQuery(req.getQueryString());

        String title = "[%s][%s] %s %s - %d".formatted(env, service, method, path, status.value());
        String desc  = """
        • message: %s
        • client: %s
        • ua: %s
        • query: %s
        • exception: %s
        """.formatted(message, ip, ua, query, safeClass(ex));

        discord.send(toDiscordJson(title, desc));
    }

    /**
     * 완전한 Unhandled 예외용(메시지/상태 없이 터진 경우).
     */
    public void notifyUnhandled(Exception ex, HttpServletRequest req) {
        String path = safe(req.getRequestURI());
        String method = safe(req.getMethod());

        String dedupKey = "500|" + path + "|UNHANDLED";
        if (isDuplicated(dedupKey)) return;

        String ua = maskUA(req.getHeader("User-Agent"));
        String ip = maskIP(req.getRemoteAddr());
        String query = maskQuery(req.getQueryString());

        String title = "[%s][%s] %s %s - 500".formatted(env, service, method, path);
        String desc  = """
        • message: UNHANDLED EXCEPTION
        • client: %s
        • ua: %s
        • query: %s
        • exception: %s
        """.formatted(ip, ua, query, safeClass(ex));

        discord.send(toDiscordJson(title, desc));
    }

    // ===== util =====
    private boolean isDuplicated(String key) {
        long now = Instant.now().toEpochMilli();
        Long last = dedupCache.get(key);
        if (last != null && now - last < dedupWindowMs) return true;
        dedupCache.put(key, now);
        return false;
    }

    private String toDiscordJson(String title, String desc) {
        return """
    {
      "embeds": [{
        "title": %s,
        "description": %s
      }]
    }
    """.formatted(json(title), json(desc));
    }

    private String json(String s){
        if (s == null) return "\"\"";
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")   // ✅ 줄바꿈 escape
                .replace("\r", "") + "\"";
    }
    private String safe(String s){ return s == null ? "" : s; }
    private String trim(String s, int n){ return s.length() <= n ? s : s.substring(0, n); }
    private String maskUA(String ua){ return ua == null ? "" : trim(ua, 180); }
    private String maskIP(String ip){ return ip == null ? "" : ip.replaceAll("(\\d+\\.\\d+\\.)(\\d+)\\.(\\d+)", "$1***.***"); }
    private String maskQuery(String q){ return q == null ? "" : q.replaceAll("(?i)(token|authorization)=[^&]*","$1=***"); }
    private String safeClass(Throwable e){ return e == null ? "-" : e.getClass().getSimpleName(); }
}