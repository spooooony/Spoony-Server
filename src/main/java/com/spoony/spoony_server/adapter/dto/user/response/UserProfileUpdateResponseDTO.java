package com.spoony.spoony_server.adapter.dto.user.response;

import java.time.LocalDate;

public record UserProfileUpdateResponseDTO(
        String userName,
        String regionName,
        String introduction,
        LocalDate birth,
        Long imageLevel) {

    public static UserProfileUpdateResponseDTO of(String userName,
                                                  String regionName,
                                                  String introduction,
                                                  LocalDate birth,
                                                  Long imageLevel) {
        return new UserProfileUpdateResponseDTO(userName, regionName, introduction, birth, imageLevel);
    }
}
