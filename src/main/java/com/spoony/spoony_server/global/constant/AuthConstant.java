package com.spoony.spoony_server.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstant {
    public static final String USER_ID = "userId";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    // KAKAO
    public static final String GRANT_TYPE = "KakaoAK ";
    public static final String TARGET_ID_TYPE = "user_id";
}
