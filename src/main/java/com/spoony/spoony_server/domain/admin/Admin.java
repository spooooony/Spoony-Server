package com.spoony.spoony_server.domain.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Admin {
    private Long adminId;
    private String email;
    private String password;
}
