package com.spoony.spoony_server.adapter.in.web.post;

import com.spoony.spoony_server.adapter.dto.post.*;
import com.spoony.spoony_server.adapter.dto.user.UserDetailResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.post.*;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserNameCheckCommand;
import com.spoony.spoony_server.application.port.in.post.*;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostGetUseCase postGetUseCase;
    private final PostCreateUseCase postCreateUseCase;
    private final PostGetCategoriesUseCase postGetCategoriesUseCase;
    private final PostScoopPostUseCase postScoopPostUseCase;
    private final PostDeleteUseCase postDeleteUseCase;
    private final PostUpdateUseCase postUpdateUseCase;

    @GetMapping("/{postId}")
    @Operation(summary = "게시물 조회 API", description = "특정 게시물의 상세 정보를 조회하는 API")
    public ResponseEntity<ResponseDTO<PostResponseDTO>> getPost(
            @UserId Long userId,
            @PathVariable long postId) {
        PostGetCommand command = new PostGetCommand(postId, userId);
        PostResponseDTO postResponse = postGetUseCase.getPostById(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postResponse));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시물 등록 API", description = "새로운 게시물을 등록하는 API")
    public ResponseEntity<ResponseDTO<Void>> createPost(
            @UserId Long userId,
            @RequestPart("data")
            @Parameter(description = "게시물 생성 요청 데이터 (JSON 형식)")
            PostCreateRequestDTO postCreateRequestDTO,
            @RequestPart("photos")
            @Parameter(description = "게시물에 첨부할 사진 리스트 (이미지 파일)")
            List<MultipartFile> photos
    ) throws IOException {
        PostPhotoSaveCommand photoSaveCommand = new PostPhotoSaveCommand(photos);
        List<String> photoUrlList = postCreateUseCase.savePostImages(photoSaveCommand);

        PostCreateCommand command = new PostCreateCommand(
                userId,
                postCreateRequestDTO.description(),
                postCreateRequestDTO.value(),
                postCreateRequestDTO.cons(),
                postCreateRequestDTO.placeName(),
                postCreateRequestDTO.placeAddress(),
                postCreateRequestDTO.placeRoadAddress(),
                postCreateRequestDTO.latitude(),
                postCreateRequestDTO.longitude(),
                postCreateRequestDTO.categoryId(),
                postCreateRequestDTO.menuList(),
                photoUrlList
        );

        postCreateUseCase.createPost(command);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/categories")
    @Operation(summary = "전체 카테고리 정보 조회 API", description = "전체 카테고리 정보를 조회하는 API")
    public ResponseEntity<ResponseDTO<CategoryMonoListResponseDTO>> getAllCategories() {
        CategoryMonoListResponseDTO categoryMonoListResponseDTO = postGetCategoriesUseCase.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(categoryMonoListResponseDTO));
    }

    @GetMapping("/categories/food")
    @Operation(summary = "음식 카테고리 정보 조회 API", description = "음식 카테고리 정보를 조회하는 API")
    public ResponseEntity<ResponseDTO<CategoryMonoListResponseDTO>> getFoodCategories() {
        CategoryMonoListResponseDTO categoryMonoListResponseDTO = postGetCategoriesUseCase.getFoodCategories();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(categoryMonoListResponseDTO));
    }

    @PostMapping("/scoop")
    @Operation(summary = "특정 게시물 떠먹기 API", description = "특정 게시물을 떠먹는 API")
    public ResponseEntity<ResponseDTO<Void>> scoopPost(
            @UserId Long userId,
            @RequestBody ScoopPostRequestDTO scoopPostRequestDTO) {
        PostScoopPostCommand command = new PostScoopPostCommand(userId, scoopPostRequestDTO.postId());
        postScoopPostUseCase.scoopPost(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시물 삭제 API", description = "게시물을 삭제하는 API")
    public ResponseEntity<ResponseDTO<Void>> deletePost(
            @UserId Long userId,
            @PathVariable long postId) {
        PostDeleteCommand command = new PostDeleteCommand(postId);
        postDeleteUseCase.deletePost(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시물 수정 API", description = "게시물을 수정하는 API")
    public ResponseEntity<ResponseDTO<Void>> updatePost(
            @UserId Long userId,
            @RequestPart("data")
            @Parameter(description = "게시물 수정 요청 데이터 (JSON 형식)")
            PostUpdateRequestDTO postUpdateRequestDTO,
            @RequestPart("photos")
            @Parameter(description = "게시물 수정 사진 리스트 (이미지 파일)")
            List<MultipartFile> photos
    ) throws IOException {
        // 사진 삭제 후 재업로드
        PostDeleteCommand deleteCommand = new PostDeleteCommand(postUpdateRequestDTO.postId());
        postDeleteUseCase.deletePhotos(deleteCommand);

        PostPhotoSaveCommand photoSaveCommand = new PostPhotoSaveCommand(photos);
        List<String> photoUrlList = postCreateUseCase.savePostImages(photoSaveCommand);

        PostUpdateCommand command = new PostUpdateCommand(
                postUpdateRequestDTO.postId(),
                postUpdateRequestDTO.description(),
                postUpdateRequestDTO.value(),
                postUpdateRequestDTO.cons(),
                postUpdateRequestDTO.categoryId(),
                postUpdateRequestDTO.menuList(),
                photoUrlList
        );

        postUpdateUseCase.updatePost(command);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }



    @GetMapping
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


    @GetMapping("/{userId}")
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


}
//public interface FeedGetUseCase {
//    FeedListResponseDTO getFeedListByUserId(FeedGetCommand command);
//}


//UserGetCommand command = new UserGetCommand(userId);
//UserDetailResponseDTO userDetailResponseDTO = userGetUseCase.getUserDetailInfo(command);
//        return ResponseEntity.ok(ResponseDTO.success(userDetailResponseDTO));


//public interface UserGetUseCase {
//    UserResponseDTO getUserInfo(UserGetCommand command);
//    UserDetailResponseDTO getUserDetailInfo(UserGetCommand command);
//    //UserDetailResponseDTO getOtherUserDetailInfo(UserGetCommand command);
//    Boolean isUsernameDuplicate(UserNameCheckCommand command);
//
//}
