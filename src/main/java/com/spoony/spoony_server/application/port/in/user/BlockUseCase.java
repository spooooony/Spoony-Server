package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;

public interface BlockUseCase {
	void changeStatus(Long blockerId, Long blockedId, BlockStatus newStatus);
	void follow(Long userId, Long targetUserId);
	void unfollow(Long userId, Long targetUserId);
	void block(Long userId, Long targetUserId);
	void unblock(Long userId, Long targetUserId);
	void report(Long userId, Long targetUserId);
}
