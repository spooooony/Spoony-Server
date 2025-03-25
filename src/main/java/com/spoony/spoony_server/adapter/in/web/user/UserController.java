package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserNameCheckCommand;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserGetUseCase userGetUseCase;

    @GetMapping
    @Operation(summary = "사용자 정보 조회 API", description = "특정 사용자의 상세 정보를 조회하는 API (Token 기준)")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserInfo(
            @UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        UserResponseDTO userResponseDTO = userGetUseCase.getUserInfo(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userResponseDTO));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 정보 조회 API", description = "특정 사용자의 상세 정보를 조회하는 API (Id 기준)")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserInfoById(
            @PathVariable Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        UserResponseDTO userResponseDTO = userGetUseCase.getUserInfo(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userResponseDTO));
    }

    @GetMapping("/exists")
    @Operation(summary = "사용자 닉네임 중복 확인 API", description = "사용자의 닉네임이 중복되는지 확인하는 API")
    public ResponseEntity<ResponseDTO<Boolean>> checkUsernameDuplicate(
            @RequestParam String userName) {
        UserNameCheckCommand command = new UserNameCheckCommand(userName);
        boolean isDuplicate = userGetUseCase.isUsernameDuplicate(command);
        return ResponseEntity.ok(ResponseDTO.success(isDuplicate));
    }
}
