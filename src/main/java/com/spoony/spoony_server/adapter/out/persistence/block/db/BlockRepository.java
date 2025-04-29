package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {

    // 차단 관계를 삭제할 때, blocker와 blocked의 userId를 기준으로 삭제
    void deleteByBlocker_userIdAndBlocked_userId(Long fromUserId, Long toUserId);

    // 차단 관계가 존재하는지 확인
    boolean existsByBlocker_userIdAndBlocked_userId(Long fromUserId, Long toUserId);

    @Query("SELECT b.blocked.userId FROM BlockEntity b WHERE b.blocker.userId = :blockerUserId")
    List<Long> findBlockedUserIdsByBlockerUserId(@Param("blockerUserId") Long blockerUserId);



//    void deleteByBlocker_UserIdAndBlocked_UserId(Long fromUserId, Long toUserId);
//    boolean existsByBlockerIdAndBlockedId(Long fromUserId, Long toUserId);
//    List<Long> findBlockedIdsByBlockerId(Long blockerId);
}
