package com.spoony.spoony_server.adapter.out.persistence.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.out.persistence.post.db.FollowRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.FollowMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.Provider;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    }

    public List<Follow> findFollowersByUserId(Long userId) {
        List<FollowEntity> followerList = followRepository.findByFollowing_UserId(userId);
        return followerList.stream()
                .map(FollowMapper::toDomain)
                .toList();
    }

    public User loadOrCreate(Provider provider, PlatformUserDTO platformUserDTO) {
        //TODO
        return null;
    }
}
