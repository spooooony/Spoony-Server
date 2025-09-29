package com.spoony.spoony_server.global.support;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfileCdn {

    private static String BASE;

    @Value("${app.profile-image.base-url}")
    private String imageBaseUrl;

    @PostConstruct
    void init() {
        BASE = imageBaseUrl;
    }

    public static String url(String fileName) {
        return BASE + fileName;
    }
}
