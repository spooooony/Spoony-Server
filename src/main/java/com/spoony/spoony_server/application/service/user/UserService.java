package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserGetUseCase {

    private final UserPort userPort;

    public UserResponseDTO getUserInfo(UserGetCommand command) {
        User user = userPort.findUserById(command.getUserId());

        return new UserResponseDTO(
                user.getUserId(),
                user.getProvider(),
                user.getProviderId(),
                user.getUserName(),
                user.getUserImage(),
                user.getRegion().getRegionName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}