package com.spoony.spoony_server.application.service.Block;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.application.port.in.user.BlockUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService implements BlockUseCase {

	private final BlockPort blockPort;
	private final UserPort userPort;
	private final FeedPort feedPort;
	private final PostPort postPort;

	@Value("${cleanup.ttl.unfollowed-days:30}")
	private int unfollowedDays;

	@Value("${cleanup.ttl.blocked-days:90}")
	private int blockedDays;

	/**
	 * 팔로우
	 * - 신규 팔로우라면 Follow + NewFollow 테이블에 추가 후 전체 백필
	 * - UNFOLLOWED였다면 → 스케쥴러 동작 여부에 따라 전체/증분 백필
	 */
	@Override
	public void follow(Long userId, Long targetUserId) {
		Optional<Block> optionalBlock  = blockPort.findByBlockerAndBlocked(userId, targetUserId);

		if (optionalBlock.isEmpty()){ //case1: 완전히 첫 팔로우 (히스토리 없음)

			Block newBlock = Block.createNew(userId, targetUserId, BlockStatus.FOLLOW, LocalDateTime.now()); //FOLLOW 관계 추가
			blockPort.saveBlock(newBlock);

			//Follow테이블에 추가(팔로우 관계 기록)
			userPort.saveFollowRelation(userId,targetUserId);

			//NewFollow테이블에 추가(백필은 Feed 조회 시점에서 진행할 예정)
			userPort.saveNewFollowRelation(userId,targetUserId);

		} else{
			Block block = optionalBlock.get();

			// case2: 기존에 UNFOLLOWED → 재팔로우
			if(block.getStatus() == BlockStatus.UNFOLLOWED){
				changeStatus(userId,targetUserId,BlockStatus.FOLLOW);
				userPort.saveFollowRelation(userId,targetUserId);

				//스케쥴러 동작 여부에 따라, 업데이트 달라짐
				if(block.getFeedPurgedAt() !=null) { // 스케쥴러가 이미 Feed 삭제한 경우 → 전체 백필 필요(전체 백필 기준->newFollow 기준)
					userPort.saveNewFollowRelation(userId,targetUserId);
				} else if(block.getStatusChangedAt() !=null) { // 스케쥴러 미동작 & 언팔 한 달 안 됐다면 → 증분만 백필(바로 백필 -> !전체백필)
					List<Post> newPosts = postPort.findByAuthorIdAndCreatedAtAfter(
						targetUserId, block.getStatusChangedAt());
					feedPort.backfillIncremental(userId, targetUserId, newPosts);
				}
			} else if(block.getStatus()== BlockStatus.BLOCKED){ //차단 상태에서 팔로우 요청 불가
				throw new BusinessException(UserErrorMessage.USER_NOT_FOUND);
			} else if (block.getStatus() == BlockStatus.REPORT){ //신고 상태에서 팔로우 요청 불가
				throw new BusinessException(UserErrorMessage.USER_NOT_FOUND);
			} else if (block.getStatus() == BlockStatus.FOLLOW){ //기존 팔로우 상태에서, 다시 팔로우 불가
				throw  new BusinessException(UserErrorMessage.ALEADY_FOLLOW);
			}

		}

		}
	/**
	 * 언팔로우
	 * - BlockStatus → UNFOLLOWED로 변경
	 * - Follow 테이블 관계 삭제(단방향)
	 * - NewFollow 즉시 제거(단방향)
	 */
	@Override
	public void unfollow(Long userId, Long targetUserId) {
		changeStatus(userId, targetUserId, BlockStatus.UNFOLLOWED);
		userPort.deleteFollowRelation(userId, targetUserId);//단방향
		userPort.deleteNewFollowRelation(userId, targetUserId); //단방향
	}


	/**
	 * 차단
	 * - BlockStatus → BLOCKED로 변경
	 * - Follow 관계 삭제 (양방향)
	 * - NewFollow 관계 삭제 (양방향)
	 * - Feed 삭제 (양방향, 논리적)
	 */
	@Override
	public void block(Long userId, Long targetUserId) {
		changeStatus(userId, targetUserId, BlockStatus.BLOCKED);
		userPort.deleteFollowRelation(userId, targetUserId); //양방향
		userPort.deleteFollowRelation(targetUserId, userId);

		userPort.deleteNewFollowRelation(userId, targetUserId);//양방향
		userPort.deleteNewFollowRelation(targetUserId, userId);

	}


	/**
	 * 차단 해제
	 * - BLOCKED → UNFOLLOWED
	 */
	@Override
	public void unblock(Long userId, Long targetUserId) {
		changeStatus(userId, targetUserId, BlockStatus.UNFOLLOWED);
	}

	@Override
	public void report(Long userId, Long targetUserId) {
		changeStatus(userId, targetUserId, BlockStatus.REPORT);

		userPort.deleteFollowRelation(userId, targetUserId);
		userPort.deleteFollowRelation(targetUserId, userId);

		userPort.deleteNewFollowRelation(userId, targetUserId);
		userPort.deleteNewFollowRelation(targetUserId, userId);

		feedPort.deleteBidirectional(userId, targetUserId);
	}

	@Override
	public void changeStatus(Long blockerId, Long blockedId, BlockStatus newStatus) {
		LocalDateTime now = LocalDateTime.now();
		Block block = blockPort.findByBlockerAndBlocked(blockerId, blockedId) // Block 행이 없으면 =>  새로 생성
			.orElse(Block.createNew(blockerId, blockedId, newStatus, now));

		block.updateStatus(newStatus, now, unfollowedDays, blockedDays); // Block 도메인 객체 내부에서 상태 갱신 & expireAt 설정
		blockPort.saveBlock(block);
	}
}