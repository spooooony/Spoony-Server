package com.spoony.spoony_server.domain.user.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.user.dto.response.UserResponseDTO;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO getUserInfo(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        return new UserResponseDTO(
                userEntity.getUserId(),
                userEntity.getUserEmail(),
                userEntity.getUserName(),
                userEntity.getUserImage(),
                userEntity.getRegion().getRegionName(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
}
