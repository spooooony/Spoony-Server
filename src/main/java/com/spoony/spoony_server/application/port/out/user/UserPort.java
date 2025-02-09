package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface UserPort {
    User findUserById(Long userId);
    List<Follow> findFollowersByUserId(Long userId);
}
