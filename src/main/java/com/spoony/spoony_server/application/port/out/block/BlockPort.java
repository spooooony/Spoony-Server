package com.spoony.spoony_server.application.port.out.block;

import com.spoony.spoony_server.domain.block.Block;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;

import java.util.List;

public interface BlockPort {
    void saveUserBlockRelation(Long fromUserId, Long toUserId);
    void deleteUserBlockRelation(Long fromUserId, Long toUserId);
    boolean existsBlockUserRelation(Long fromUserId, Long toUserId);
    List<Long> getBlockedUserIds(Long userId);
}

