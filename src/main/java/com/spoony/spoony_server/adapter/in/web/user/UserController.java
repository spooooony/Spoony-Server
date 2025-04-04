package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.adapter.dto.user.UserDetailResponseDTO;
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

    @GetMapping("/{userId}/detail")
    @Operation(
            summary = "타인 마이페이지용 사용자 정보 조회 API",
            description = """
        특정 사용자의 마이페이지 상세 정보를 조회하는 API입니다.

        - 응답에는 항상 **introduction** 필드가 포함됩니다.
        - 사용자가 소개글을 작성하지 않은 경우, 서버에서 기본 소개글("안녕! 나는 어떤 스푼이냐면...")을 대신 제공합니다.
        - **introduction** 필드를 그대로 마이페이지에 활용하면 됩니다!
        """
    )
    public ResponseEntity<ResponseDTO<UserDetailResponseDTO>> getOtherUserDetail(
            @PathVariable Long userId
    ) {
        UserGetCommand command = new UserGetCommand(userId);
        UserDetailResponseDTO userDetailResponseDTO = userGetUseCase.getUserDetailInfo(command);
        return ResponseEntity.ok(ResponseDTO.success(userDetailResponseDTO));
    }



    @GetMapping("/detail")
    @Operation(
            summary = "내 마이페이지용 사용자 정보 조회 API",
            description = """
        자기 자신의 마이페이지 상세 정보를 조회하는 API입니다.

        - 응답에는 항상 **introduction** 필드가 포함됩니다.
        - 사용자가 소개글을 작성하지 않은 경우, 서버에서 기본 소개글("안녕! 나는 어떤 스푼이냐면...")을 대신 제공합니다.
        - **introduction** 필드를 그대로 마이페이지에 활용하면 됩니다!
        """
    )
    public ResponseEntity<ResponseDTO<UserDetailResponseDTO>> getUserDetail(
            @UserId Long userId

    ) {
        UserGetCommand command = new UserGetCommand(userId);
        UserDetailResponseDTO userDetailResponseDTO = userGetUseCase.getUserDetailInfo(command);
        return ResponseEntity.ok(ResponseDTO.success(userDetailResponseDTO));
    }



    @GetMapping("/user/followers")
    @Operation(summary = "팔로워 조회 API", description = "로그인한 사용자를 팔로우하는 유저 목록을 조회하는 API")
    public ResponseEntity<Void> getFollowers(@UserId Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/user/followings")
    @Operation(summary = "팔로잉 조회 API", description = "로그인한 사용자가 팔로우하는 유저 목록을 조회하는 API")
    public ResponseEntity<Void> getFollowings(@UserId Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "마이페이지에서 사용자의 프로필을 수정하는 API")
    public ResponseEntity<Void> updateUserProfile(
            @UserId Long userId,
            @RequestBody Object profileUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/block")
    @Operation(summary = "유저 차단 API", description = "다른 사용자를 차단하는 API")
    public ResponseEntity<Void> blockUser(
            @UserId Long requesterId,
            @RequestBody Object requestDTO //신고할 유저의 userId 포함
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


}
