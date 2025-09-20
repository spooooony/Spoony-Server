package com.spoony.spoony_server.application.service.feed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spoony.spoony_server.domain.user.BlockStatus;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.domain.user.Block;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class FeedCleanupScheduler {

	private final BlockPort blockPort;
	private final FeedPort feedPort;

	// 배치 실행 시 한 번에 몇 건씩 가져올지 (페이징 처리용)
	@Value("${batch.size:2000}") int pageSize;
	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	public void cleanupExpiredFeeds(){
		LocalDateTime now = LocalDateTime.now();

		List<BlockStatus> statuses = new ArrayList<>();
		statuses.add(BlockStatus.UNFOLLOWED);
		statuses.add(BlockStatus.BLOCKED);

		for (int page = 0; ; page++) {
			Pageable pageable = PageRequest.of(page, pageSize);
			List<Block> blocks = blockPort.findExpiredBlocks(statuses, now, pageable);

			// 더 이상 처리할 데이터가 없으면 종료
			if (blocks.isEmpty()) {
				break;
			}

			for (Block block : blocks) {
				switch (block.getStatus()) {
					case UNFOLLOWED -> {
						// UNFOLLOWED → 단방향 삭제
						feedPort.deleteOneWay(block.getBlocker().getUserId(),
							block.getBlocked().getUserId());
					}
					case BLOCKED -> {
						// BLOCKED → 양방향 삭제
						feedPort.deleteBidirectional(block.getBlocker().getUserId(),
							block.getBlocked().getUserId());
					}
					default -> throw new IllegalStateException(
						"스케줄러 대상이 아닌 상태 발견: " + block.getStatus()
					);
				}

				// feed 삭제 시각 기록 → feed_purged_at 업데이트
				blockPort.markFeedPurgedAt(
					block.getBlocker().getUserId(),
					block.getBlocked().getUserId(),
					now
				);
			}
		}
	}
}