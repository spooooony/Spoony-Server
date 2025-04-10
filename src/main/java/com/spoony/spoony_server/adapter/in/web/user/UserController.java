package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.adapter.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.ReviewAmountResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.*;
import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.user.UserFollowUseCase;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.application.port.in.user.UserSearchUseCase;
import com.spoony.spoony_server.application.port.in.user.UserUpdateUseCase;
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
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserSearchUseCase userSearchUseCase;
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

    @GetMapping("/profile")
    @Operation(
            summary = "프로필 수정 진입 시 사용자 기본 정보 조회 API",
            description = """
        프로필 수정 페이지 진입 시, 사용자의 기존 입력 정보를 조회합니다.
        
        - 닉네임, 생일, 활동지역 등 프로필 구성 필드를 반환합니다.
        - 생일과 활동지역은 사용자가 스킵할 수 있기 때문에, 기본값이 포함되어 반환됩니다.
        """
    )
    public ResponseEntity<ResponseDTO<UserProfileUpdateResponseDTO>> getUserEditInfo(
            @UserId Long userId
    ) {
        UserGetCommand command = new UserGetCommand(userId);

        UserProfileUpdateResponseDTO responseDTO = userGetUseCase.getUserProfileInfo(command);
        return ResponseEntity.ok(ResponseDTO.success(responseDTO));
    }
    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "마이페이지에서 사용자의 프로필을 수정하는 API입니다.")
    public ResponseEntity<ResponseDTO<Void>> updateUserProfile(
            @UserId Long userId,
            @RequestBody UserProfileUpdateRequestDTO userUpdateRequestDTO
    ) {

        UserUpdateCommand command = new UserUpdateCommand(userId,userUpdateRequestDTO.userName(),userUpdateRequestDTO.regionId(),userUpdateRequestDTO.introduction(),userUpdateRequestDTO.birth());
        userUpdateUseCase.updateUserProfile(command);
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

//    public ResponseEntity<ResponseDTO<Void>> followUser(
//            @UserId Long userId,
//            @RequestBody UserFollowRequestDTO requestDTO
//    ) {
//        UserFollowCommand command = new UserFollowCommand(
//                userId,
//                requestDTO.targetUserId()
//        );
//        userFollowUseCase.createFollow(command);
//
//        return ResponseEntity.ok(ResponseDTO.success(null));
//    }

    @GetMapping("/me/reviews")
    @Operation(
            summary = "내가 작성한 리뷰 전체 조회 API",
            description = """
    마이페이지에서 **자신이 작성한 리뷰 목록**을 조회하는 API입니다.
    - 서버는 로그인된 사용자 정보를 기준으로 리뷰 목록을 조회합니다.
    
    """
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getAllMyPosts(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId); //user 객체

        FeedListResponseDTO postListResponse = postGetUseCase.getPostsByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postListResponse));
    }

    @GetMapping("/me/review-amount")
    @Operation(
            summary = "내가 작성한 리뷰 개수 조회 API",
            description = """
    마이페이지에서 **해당 사용자가 작성한 리뷰 개수**를 조회하는 API입니다.

    """
    )
    public ResponseEntity<ResponseDTO<ReviewAmountResponseDTO>> getMyReviewAmount(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        ReviewAmountResponseDTO reviewAmountResponse = postGetUseCase.getPostAmountByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(reviewAmountResponse));
    }

    @GetMapping("/{userId}/reviews")
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

    @GetMapping("/{userId}/review-amount")
    @Operation(
            summary = "특정 사용자 리뷰 개수 조회 API",
            description = """
    다른 사용자의 마이페이지에서 **해당 사용자가 작성한 리뷰 개수**를 조회하는 API입니다.
    - **userId**는 path parameter로 전달받습니다.
   
    """
    )
    public ResponseEntity<ResponseDTO<ReviewAmountResponseDTO>> getReviewAmountByUserId(@PathVariable Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        ReviewAmountResponseDTO reviewAmountResponse = postGetUseCase.getPostAmountByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(reviewAmountResponse));
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

//    @GetMapping("/search")
//    @Operation(summary = "유저 검색 API", description = "**탐색>검색**에서 **유저 검색 결과**를 보여주는  API입니다")
//    public ResponseEntity<ResponseDTO<List<UserSimpleResponseDTO>>> searchUsers(@RequestParam String query) {
//        UserSearchCommand command = new UserSearchCommand(query);
//        List<UserSimpleResponseDTO> result = userGetUseCase.getUserSimpleInfoBySearch(command);
//
//        return ResponseEntity.ok(ResponseDTO.success(result));
//    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색 API", description = "검색어를 통해 유저를 검색하는 API")
    public ResponseEntity<ResponseDTO<UserSearchResultListDTO>> searchLocations(
                @RequestParam String query) {
            UserSearchCommand command = new UserSearchCommand(query);
            UserSearchResultListDTO userSearchList = userSearchUseCase.searchUsersByQuery(command);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userSearchList));
        }


}
