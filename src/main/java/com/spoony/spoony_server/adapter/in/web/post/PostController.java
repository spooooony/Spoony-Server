package com.spoony.spoony_server.adapter.in.web.post;

import com.spoony.spoony_server.adapter.dto.post.request.PostCreateRequestDTO;
import com.spoony.spoony_server.adapter.dto.post.request.PostUpdateRequestDTO;
import com.spoony.spoony_server.adapter.dto.post.response.CategoryMonoListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.PostResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.response.PostSearchResultListDTO;
import com.spoony.spoony_server.application.event.PostCreatedEvent;
import com.spoony.spoony_server.application.port.command.post.*;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.post.*;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.request.ScoopPostRequestDTO;
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
    private final PostSearchUseCase postSearchUseCase;

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
            @RequestBody PostCreateRequestDTO postCreateRequestDTO
    ) {

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
                postCreateRequestDTO.photoUrlList()
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
            @RequestPart(value = "photos", required = false)
            @Parameter(description = "게시물 수정 사진 리스트 (이미지 파일)")
            List<MultipartFile> photos
    ) throws IOException {
        List<String> photoUrlList = List.of();

        if (photos != null && photos.stream().anyMatch(photo -> !photo.isEmpty())) {
            PostPhotoSaveCommand photoSaveCommand = new PostPhotoSaveCommand(photos);
            photoUrlList = postCreateUseCase.savePostImages(photoSaveCommand);
        }

        PostUpdateCommand command = new PostUpdateCommand(
                postUpdateRequestDTO.postId(),
                postUpdateRequestDTO.description(),
                postUpdateRequestDTO.value(),
                postUpdateRequestDTO.cons(),
                postUpdateRequestDTO.categoryId(),
                postUpdateRequestDTO.menuList(),
                photoUrlList,
                postUpdateRequestDTO.deleteImageUrlList()
        );

        postUpdateUseCase.updatePost(command);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }

    @GetMapping("/search")
    @Operation(summary = "리뷰 검색 API", description = "검색어를 통해 리뷰를 검색하는 API")
    public ResponseEntity<ResponseDTO<PostSearchResultListDTO>> searchLocations(
            @UserId Long userId,
            @RequestParam String query) {

        UserGetCommand userGetCommand = new UserGetCommand(userId);
        PostSearchCommand searchCommand = new PostSearchCommand(query);

        PostSearchResultListDTO postSearchList = postSearchUseCase.searchReviewsByQuery(userGetCommand,searchCommand);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postSearchList));
    }
}
