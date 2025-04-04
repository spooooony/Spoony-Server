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


    //Long countPostByUserId(Long userId);

    // 내가 팔로우한 사람 수 (팔로잉 수)
    Long countFollowerByUserId(Long userId);


    // 나를 팔로우한 사람 수 (팔로워 수)
    Long countFollowingByUserId(Long userId);


}
