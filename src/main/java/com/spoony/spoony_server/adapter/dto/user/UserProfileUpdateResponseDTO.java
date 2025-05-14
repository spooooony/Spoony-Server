package com.spoony.spoony_server.adapter.dto.user;


import java.time.LocalDate;

public record UserProfileUpdateResponseDTO(
        String userName,
        String regionName,
        String introduction,
        LocalDate birth,
        Long imageLevel) {
}
