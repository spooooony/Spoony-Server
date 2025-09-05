package com.spoony.spoony_server.adapter.in.web.user;

import com.spoony.spoony_server.adapter.dto.post.response.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.request.UserBlockRequestDTO;
import com.spoony.spoony_server.adapter.dto.user.request.UserFollowRequestDTO;
import com.spoony.spoony_server.adapter.dto.user.request.UserProfileUpdateRequestDTO;
import com.spoony.spoony_server.adapter.dto.user.response.*;
import com.spoony.spoony_server.application.port.command.user.BlockCheckCommand;
import com.spoony.spoony_server.application.port.command.user.BlockUserCommand;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.BlockCheckUseCase;
import com.spoony.spoony_server.application.port.in.user.BlockUserCreateUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.user.*;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.global.message.business.BlockErrorMessage;
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
    private  final UserFollowUseCase userFollowUseCase;
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserSearchUseCase userSearchUseCase;
    private final PostGetUseCase postGetUseCase;
    private final ProfileImageGetUseCase profileImageGetUseCase;
    private final RegionGetUseCase regionGetUseCase;
    private final BlockUserCreateUseCase blockUserCreateUseCase;
    private final BlockCheckUseCase blockCheckUseCase;
    private final BlockUseCase blockUseCase;

    @GetMapping
    @Operation(summary = "사용자 정보 조회 API", description = "특정 사용자의 상세 정보를 조회하는 API (Token 기준)")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserInfo(
            @UserId Long userId) {
        RelatedUserGetCommand relatedUserGetCommand = new RelatedUserGetCommand(userId,userId);
        UserResponseDTO userResponseDTO = userGetUseCase.getUserInfo(relatedUserGetCommand,null);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userResponseDTO));
    }

    @GetMapping("/{targetUserId}")
    @Operation(summary = "사용자 정보 조회 API", description = "특정 사용자의 상세 정보를 조회하는 API (Id 기준)")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserInfoById(
            @UserId Long userId,
            @PathVariable Long targetUserId) {

        UserGetCommand userGetCommand = new UserGetCommand(targetUserId);
        RelatedUserGetCommand relatedUserGetCommand = new RelatedUserGetCommand(userId,targetUserId);
        UserFollowCommand userFollowCommand = new UserFollowCommand(userId,targetUserId);

        BlockCheckCommand blockCheckCommand = new BlockCheckCommand(userId, targetUserId);

        if (blockCheckUseCase.isBlockedByBlockingOrReporting(blockCheckCommand)) { //"isBlockedByBlockingOrReporting" -> '유저'에 대한 차단&신고 처리
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseDTO.fail(BlockErrorMessage.USER_BLOCKED));
        }

        UserResponseDTO userResponseDTO = userGetUseCase.getUserInfo(relatedUserGetCommand,userFollowCommand);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userResponseDTO));
    }

    @GetMapping("/exists")
    @Operation(summary = "사용자 닉네임 중복 확인 API", description = "사용자의 닉네임이 중복되는지 확인합니다.")
    public ResponseEntity<ResponseDTO<Boolean>> checkUsernameDuplicate(
            @RequestParam String userName) {
        UserNameCheckCommand command = new UserNameCheckCommand(userName);
        boolean isDuplicate = userGetUseCase.isUsernameDuplicate(command);
        return ResponseEntity.ok(ResponseDTO.success(isDuplicate));
    }

    @GetMapping("/followers")
    @Operation(summary = "팔로워 조회 API", description = "로그인한 사용자를 팔로우하는 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<FollowListResponseDTO>> getMyFollowers(@UserId Long userId) {
        FollowGetCommand command = new FollowGetCommand(userId,userId);
        FollowListResponseDTO followListResponse = userGetUseCase.getFollowers(command);
        return ResponseEntity.ok(ResponseDTO.success(followListResponse));
    }

    @GetMapping("/followers/{targetUserId}")
    @Operation(summary = "팔로워 조회 API", description = "타유저를 팔로우하는 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<FollowListResponseDTO>> getTargetUserFollowers(@UserId Long userId,@PathVariable Long targetUserId) {
        FollowGetCommand command = new FollowGetCommand(userId,targetUserId);
        FollowListResponseDTO followListResponse = userGetUseCase.getFollowers(command);
        return ResponseEntity.ok(ResponseDTO.success(followListResponse));
    }

    @GetMapping("/followings")
    @Operation(summary = "팔로잉 조회 API", description = "로그인한 사용자가 팔로우하는 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<FollowListResponseDTO>> getMyFollowings(@UserId Long userId) {
        FollowGetCommand command = new FollowGetCommand(userId,userId);
        FollowListResponseDTO followings = userGetUseCase.getFollowings(command);
        return ResponseEntity.ok(ResponseDTO.success(followings));
    }

    @GetMapping("/followings/{targetUserId}")
    @Operation(summary = "팔로잉 조회 API", description = "타유저가 팔로우하는 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<FollowListResponseDTO>> getTargetUserFollowings(@UserId Long userId, @PathVariable Long targetUserId) {
        FollowGetCommand command = new FollowGetCommand(userId,targetUserId);
        FollowListResponseDTO followings = userGetUseCase.getFollowings(command);
        return ResponseEntity.ok(ResponseDTO.success(followings));
    }

    @GetMapping("/profile")
    @Operation(
            summary = "프로필 수정 진입 시 사용자 기본 정보 조회 API",
            description = "프로필 수정 페이지 진입 시, 사용자의 기존 입력 정보를 조회합니다.")
    public ResponseEntity<ResponseDTO<UserProfileUpdateResponseDTO>> getUserEditInfo(
            @UserId Long userId
    ) {
        UserGetCommand command = new UserGetCommand(userId);
        UserProfileUpdateResponseDTO responseDTO = userGetUseCase.getUserProfileInfo(command);
        return ResponseEntity.ok(ResponseDTO.success(responseDTO));
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "마이페이지에서 사용자의 프로필을 수정합니다.")
    public ResponseEntity<ResponseDTO<Void>> updateUserProfile(
            @UserId Long userId,
            @RequestBody UserProfileUpdateRequestDTO userUpdateRequestDTO
    ) {
        UserUpdateCommand command = new UserUpdateCommand(userId,userUpdateRequestDTO.userName(),userUpdateRequestDTO.regionId(),userUpdateRequestDTO.introduction(),userUpdateRequestDTO.birth(),userUpdateRequestDTO.imageLevel());
        userUpdateUseCase.updateUserProfile(command);
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/profile/images")
    public ResponseEntity<ResponseDTO<ProfileImageListResponseDTO>> getAvailableProfileImages(@UserId Long userId){
        UserGetCommand command = new UserGetCommand(userId);
        ProfileImageListResponseDTO profileImageListResponse = profileImageGetUseCase.getAvailableProfileImages(command);
        return ResponseEntity.ok(ResponseDTO.success(profileImageListResponse));
    }

    @GetMapping("/reviews")
    @Operation(
            summary = "내가 작성한 리뷰 전체 조회 API",
            description = "마이페이지에서 자신이 작성한 리뷰 목록을 조회합니다."
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getAllMyPosts(@UserId Long userId) {
        UserReviewGetCommand command = new UserReviewGetCommand(userId,userId,null);
        FeedListResponseDTO postListResponse = postGetUseCase.getPostsByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postListResponse));
    }

    @GetMapping(value = "/reviews/{targetUserId}", produces = "application/json; charset=UTF-8")
    @Operation(
            summary = "특정 사용자 리뷰 전체 조회 API",
            description = "다른 사용자의 마이페이지에서 해당 사용자가 작성한 리뷰 목록을 조회합니다."
    )
    public ResponseEntity<ResponseDTO<FeedListResponseDTO>> getAllPostsByUserId(@UserId Long userId, @PathVariable Long targetUserId,@RequestParam(defaultValue = "false") boolean isLocalReview) {
        UserReviewGetCommand command = new UserReviewGetCommand(userId,targetUserId,isLocalReview);
        BlockCheckCommand blockCheckCommand = new BlockCheckCommand(userId, targetUserId);

        if (blockCheckUseCase.isBlockedByBlockingOrReporting(blockCheckCommand)) { //isBlockedByBlockingOrReporting(x) -> '게시물'에 대한 신고 거르기
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseDTO.fail(BlockErrorMessage.USER_BLOCKED));
        }
        FeedListResponseDTO postListResponse = postGetUseCase.getPostsByUserId(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postListResponse));
    }

    @PostMapping("/follow")
    @Operation(
            summary = "유저 팔로우 API",
            description = "다른 사용자를 팔로우합니다."
    )
    public ResponseEntity<ResponseDTO<Void>> followUser(
            @UserId Long userId,
            @RequestBody UserFollowRequestDTO requestDTO
    ) {
        blockUseCase.follow(userId, requestDTO.targetUserId());

        UserFollowCommand command = new UserFollowCommand(userId,requestDTO.targetUserId());
        userFollowUseCase.createFollow(command);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @DeleteMapping("/follow")
    @Operation(
            summary = "유저 팔로우 취소 API",
            description = "다른 사용자에 대한 팔로우를 취소합니다."
    )
    public ResponseEntity<ResponseDTO<Void>> unfollowUser(
            @UserId Long userId,
            @RequestBody UserFollowRequestDTO requestDTO
    ) {

        blockUseCase.unfollow(userId, requestDTO.targetUserId());
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/blockings")
    @Operation(summary = "차단한 유저 조회 API", description = "로그인한 사용자가 차단한 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<BlockListResponseDTO>> getBlockings(@UserId Long userId) {
        UserGetCommand command = new UserGetCommand(userId);
        BlockListResponseDTO blockedUsers = userGetUseCase.getBlockings(command);
        return ResponseEntity.ok(ResponseDTO.success(blockedUsers));
    }
    @PostMapping("/block")
    @Operation(summary = "유저 차단 API", description = "다른 사용자를 차단합니다.")
    public ResponseEntity<ResponseDTO<Void>> blockUser(
            @UserId Long userId,
            @RequestBody UserBlockRequestDTO requestDTO
    ) {
        BlockUserCommand command = new BlockUserCommand(
                userId,
                requestDTO.targetUserId()
        );
        blockUserCreateUseCase.createUserBlock(command);
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @DeleteMapping("/block")
    @Operation(
            summary = "유저 차단 해제 API",
            description = "다른 사용자에 대한 차단을 해제합니다."
    )
    public ResponseEntity<ResponseDTO<Void>> unBlockUser(
            @UserId Long userId,
            @RequestBody UserBlockRequestDTO requestDTO
    ) {
        blockUseCase.unblock(userId, requestDTO.targetUserId());
        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색 API", description = "검색어를 통해 유저를 검색합니다.")
    public ResponseEntity<ResponseDTO<UserSearchResponseListDTO>> searchLocations(
                @UserId Long userId,
                @RequestParam String query) {

        UserGetCommand command = new UserGetCommand(userId);
        UserSearchCommand searchCommand = new UserSearchCommand(query);
        UserSearchResponseListDTO userSearchList = userSearchUseCase.searchUsersByQuery(command, searchCommand);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(userSearchList));
    }

    @GetMapping("/region")
    @Operation(summary = "유저 활동 지역 리스트 API", description = "모든 유저 활동 지역 종류를 반환합니다.")
    public ResponseEntity<ResponseDTO<RegionListResponseDTO>> getRegion() {
        RegionListResponseDTO regionListResponseDTO = regionGetUseCase.getRegionList();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(regionListResponseDTO));
    }

    @PostMapping("/report")
    @Operation(summary = "유저 신고 API", description = "다른 사용자를 신고합니다.")
    public ResponseEntity<ResponseDTO<Void>> reportUser(
        @UserId Long userId,
        @RequestBody UserBlockRequestDTO requestDTO
    ) {
        blockUseCase.report(userId, requestDTO.targetUserId()); //
        return ResponseEntity.ok(ResponseDTO.success(null));
    }
}
