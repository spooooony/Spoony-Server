package com.spoony.spoony_server.adapter.dto.user;

import java.time.LocalDateTime;

public record UserUpdateRequestDTO (
        String userName,
        String introduction,
        LocalDateTime birth,
        String region
){
}
