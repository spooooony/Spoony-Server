package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserGetUseCase {

    private final UserRepository userRepository;

    public UserResponseDTO getUserInfo(UserGetCommand command) {
        UserEntity userEntity = userRepository.findById(command.getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

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
