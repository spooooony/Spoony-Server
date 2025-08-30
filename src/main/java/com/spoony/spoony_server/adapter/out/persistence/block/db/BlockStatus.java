package com.spoony.spoony_server.adapter.out.persistence.block.db;

public enum BlockStatus {
    FOLLOW,       // 정상 상태 (현재 관계 유효)
    BLOCKED,
    UNFOLLOWED,
    REPORT
}
