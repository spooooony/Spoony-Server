package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.domain.user.Platform;

import java.time.LocalDateTime;

public record UserProfileUpdateResponseDTO(
                                           String userName,
                                           String regionName,
                                           String introduction,
                                           LocalDateTime birth) {
}
