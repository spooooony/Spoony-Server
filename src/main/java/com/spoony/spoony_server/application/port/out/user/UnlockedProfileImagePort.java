package com.spoony.spoony_server.application.port.out.user;

import java.util.Set;

public interface UnlockedProfileImagePort {
    
    /**
     * 사용자의 잠금해제된 프로필 레벨 조회
     * @param userId 사용자 ID
     * @return 잠금해제된 레벨 Set
     */
    Set<Integer> findUnlockedLevelsByUserId(Long userId);
    
    /**
     * 새로운 프로필 레벨 잠금해제 저장
     * @param userId 사용자 ID
     * @param profileLevel 프로필 레벨
     */
    void saveUnlockedLevel(Long userId, Integer profileLevel);
    
    /**
     * 특정 레벨이 잠금해제되었는지 확인
     * @param userId 사용자 ID
     * @param profileLevel 프로필 레벨
     * @return 잠금해제 여부
     */
    boolean isLevelUnlocked(Long userId, Integer profileLevel);
}
