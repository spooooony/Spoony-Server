package com.spoony.spoony_server.adapter.dto.user;

public record UserDetailResponseDTO(
        String userName,
        String regionName,
        String introduction,
        Long followerCount,
        Long followingCount,
        boolean isFollowing
) {



}
