package com.spoony.spoony_server.adapter.out.persistence.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewFollowRepository extends JpaRepository<NewFollowEntity, Long> {


    //새로 팔로우한 유저 ID 조회
    @Query("SELECT nf.newFollowing.id FROM NewFollowEntity nf WHERE nf.newFollower.id = :userId")
    List<Long> findFollowedUserIdsByUserId(@Param("userId") Long userId);

    //특정 유저가 팔로우한 관계 조회
    @Query("SELECT COUNT(nf) > 0 FROM NewFollowEntity nf WHERE nf.newFollower.id = :followerId AND nf.newFollowing.id = :followingId")
    boolean existsNewFollowRelation(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    //NEW FOLLOW 팔로우 관계 삭제
    @Modifying
    @Query("DELETE FROM NewFollowEntity nf WHERE nf.newFollower.id = :followerId AND nf.newFollowing.id = :followingId")
    void deleteFollowRelation(@Param("followerId") Long followerId, @Param("followingId") Long followingId);


    List<NewFollowEntity> findByNewFollower_UserId(Long userId);
}
