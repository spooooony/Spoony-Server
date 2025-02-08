package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserGetUseCase userGetUseCase;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserInfo(@PathVariable Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        UserResponseDTO userResponseDTO = userGetUseCase.getUserInfo(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userResponseDTO));
    }
}
