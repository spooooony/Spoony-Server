package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.application.port.dto.user.UserResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserGetUseCase {

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
