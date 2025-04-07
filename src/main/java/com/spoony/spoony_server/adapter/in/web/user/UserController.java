package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.*;
import com.spoony.spoony_server.application.port.command.user.UserFollowCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserNameCheckCommand;
import com.spoony.spoony_server.application.port.command.user.UserSearchCommand;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.user.UserFollowUseCase;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserGetUseCase userGetUseCase;
    private  final UserFollowUseCase userFollowUseCase;
    private final PostGetUseCase postGetUseCase;

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
            @UserId Long userId,
            @PathVariable Long targetUserId
    ) {
        UserGetCommand userGetCommand = new UserGetCommand(targetUserId);
        UserFollowCommand userFollowCommand = new UserFollowCommand(userId,targetUserId);
        UserDetailResponseDTO userDetailResponseDTO = userGetUseCase.getUserDetailInfo(userGetCommand,userFollowCommand);
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
        UserDetailResponseDTO userDetailResponseDTO = userGetUseCase.getUserDetailInfo(command,null);
        return ResponseEntity.ok(ResponseDTO.success(userDetailResponseDTO));
    }


    @GetMapping("/followers")
    @Operation(summary = "팔로워 조회 API", description = "로그인한 사용자를 팔로우하는 유저 목록을 조회하는 API입니다.")
    public ResponseEntity<ResponseDTO<List<UserSimpleResponseDTO>>> getFollowers(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        List<UserSimpleResponseDTO> followers = userGetUseCase.getFollowers(command.getUserId());
        return ResponseEntity.ok(ResponseDTO.success(followers));
    }

    @GetMapping("/followings")
    @Operation(summary = "팔로잉 조회 API", description = "로그인한 사용자가 팔로우하는 유저 목록을 조회하는 API입니다.")
    public ResponseEntity<ResponseDTO<List<UserSimpleResponseDTO>>> getFollowings(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);

        List<UserSimpleResponseDTO> followings = userGetUseCase.getFollowings(command.getUserId());
        return ResponseEntity.ok(ResponseDTO.success(followings));
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "마이페이지에서 사용자의 프로필을 수정하는 API입니다.")
    public ResponseEntity<ResponseDTO<Void>> updateUserProfile(
            @UserId Long userId,
            @RequestBody UserUpdateRequestDTO userUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/me/posts")
    @Operation(
            summary = "내가 작성한 리뷰 전체 조회 API",
            description = """
    마이페이지에서 **자신이 작성한 리뷰 목록**을 조회하는 API입니다.
    - 서버는 로그인된 사용자 정보를 기준으로 게시글을 조회합니다.
    
    """
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getAllMyPosts(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId); //user 객체

        FeedListResponseDTO postListResponse = postGetUseCase.getPostsByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postListResponse));
    }


    @GetMapping("/{userId}/posts")
    @Operation(
            summary = "특정 사용자 리뷰 전체 조회 API",
            description = """
    다른 사용자의 마이페이지에서 **해당 사용자가 작성한 리뷰 목록**을 조회하는 API입니다.
    - **userId**는 path parameter로 전달받습니다.
   
    """
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getAllPostsByUserId(@PathVariable Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        FeedListResponseDTO postListResponse = postGetUseCase.getPostsByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postListResponse));
    }

    @PostMapping("/follow")
    @Operation(
            summary = "유저 팔로우 API",
            description = "다른 사용자를 팔로우하는 API."

    )
    public ResponseEntity<ResponseDTO<Void>> followUser(
            @UserId Long userId,
            @RequestBody UserFollowRequestDTO requestDTO
    ) {
        UserFollowCommand command = new UserFollowCommand(
                userId,
                requestDTO.targetUserId()
        );
        userFollowUseCase.createFollow(command);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @DeleteMapping("/unfollow")
    @Operation(
            summary = "유저 팔로우 취소 API",
            description = "다른 사용자에 대한 팔로우를 취소하는 API입니다."
    )
    public ResponseEntity<ResponseDTO<Void>> unfollowUser(
            @UserId Long userId,
            @RequestBody UserFollowRequestDTO requestDTO
    ) {
        UserFollowCommand command = new UserFollowCommand(
                userId,
                requestDTO.targetUserId()
        );
        userFollowUseCase.deleteFollow(command);
        return ResponseEntity.ok(ResponseDTO.success(null));
    }


    @PostMapping("/block")
    @Operation(summary = "유저 차단 API", description = "다른 사용자를 차단하는 API입니다.")
    public ResponseEntity<ResponseDTO<Void>> blockUser(
            @UserId Long requesterId,
            @RequestBody UserBlockRequestDTO userBlockRequestDTO
    ) {
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색 API", description = "**탐색>검색**에서 **유저 검색 결과**를 보여주는  API입니다")
    public ResponseEntity<ResponseDTO<List<UserSimpleResponseDTO>>> searchUsers(@RequestParam String query) {
        UserSearchCommand command = new UserSearchCommand(query);
        List<UserSimpleResponseDTO> result = userGetUseCase.getUserSimpleInfoBySearch(command);

        return ResponseEntity.ok(ResponseDTO.success(result));
    }

//    @GetMapping("search/history")
//    @Operation(summary = "유저 검색 기록 조회 API",description = "**탐색>검색 버튼** 누른 뒤, **유저 탭**에서의 최근 검색 기록을 조회하는 API입니다.")
//    public ResponseEntity<ResponseDTO<UserSearchHistoryResponseDTO>> getUserSearchHistory(@UserId Long userId) {
//        UserGetCommand command = new UserGetCommand(userId);
//        UserSearchHistoryResponseDTO history = userGetUseCase.getUserSearchHistory(command);
//        return ResponseEntity.ok(ResponseDTO.success(history));
//    }

}
