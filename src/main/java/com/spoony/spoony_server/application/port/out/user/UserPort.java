package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface UserPort {
    User findUserById(Long userId);
    boolean existsByUserName(String userName);
    List<Follow> findFollowersByUserId(Long userId);
    User loadOrCreate(PlatformUserDTO platformUserDTO, UserLoginDTO userLoginDTO);
}
