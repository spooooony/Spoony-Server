package com.spoony.spoony_server.application.port.out.user;

import java.util.List;

public interface BlockPort {
    void saveUserBlockRelation(Long fromUserId, Long toUserId);
    void deleteUserBlockRelation(Long fromUserId, Long toUserId);
    boolean existsBlockUserRelation(Long fromUserId, Long toUserId);
    List<Long> getBlockedUserIds(Long userId);
}

