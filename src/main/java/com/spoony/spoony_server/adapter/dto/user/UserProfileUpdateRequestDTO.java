package com.spoony.spoony_server.adapter.dto.user;


import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserProfileUpdateRequestDTO(String userName,
                                          Long regionId,
                                          String introduction,
                                          LocalDate birth,
                                          Long imageLevel) {
}

