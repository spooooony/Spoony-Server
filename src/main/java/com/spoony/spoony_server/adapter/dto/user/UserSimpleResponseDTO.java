package com.spoony.spoony_server.adapter.dto.user;

public record  UserSimpleResponseDTO (
    Long userId,
    String username,
    String location,
    boolean isFollowing
){
}
