package com.spoony.spoony_server.adapter.dto.user;

public record UserDetailResponseDTO(
        String userName,
        String region,
        String introduction,
        Long followerCount,
        Long followingCount,
        boolean isFollowing
) {



}
