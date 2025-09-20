package com.spoony.spoony_server.domain.user;

public enum BlockStatus {
    FOLLOW,       // 정상 상태 (현재 관계 유효)
    BLOCKED,
    UNFOLLOWED,
    REPORT
}
