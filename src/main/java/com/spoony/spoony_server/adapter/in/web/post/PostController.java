package com.spoony.spoony_server.adapter.in.web.post;

import com.spoony.spoony_server.application.port.command.post.PostCreateCommand;
import com.spoony.spoony_server.application.port.command.post.PostGetCommand;
import com.spoony.spoony_server.application.port.command.post.PostPhotoSaveCommand;
import com.spoony.spoony_server.application.port.command.post.PostScoopPostCommand;
import com.spoony.spoony_server.application.port.in.post.PostCreateUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetCategoriesUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.post.PostScoopPostUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.CategoryMonoListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.PostResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.ScoopPostRequestDTO;
import com.spoony.spoony_server.adapter.dto.zzim.PostCreateRequestDTO;
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

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<PostResponseDTO>> getPost(@UserId Long userId, @PathVariable long postId) {
        PostGetCommand command = new PostGetCommand(postId, userId);
        PostResponseDTO postResponse = postGetUseCase.getPostById(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postResponse));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<Void>> createPost(
            @RequestPart("data") PostCreateRequestDTO postCreateRequestDTO,
            @RequestPart("photos") List<MultipartFile> photos
    ) throws IOException {
        PostPhotoSaveCommand photoSaveCommand = new PostPhotoSaveCommand(photos);
        List<String> photoUrlList = postCreateUseCase.savePostImages(photoSaveCommand);

        PostCreateCommand command = new PostCreateCommand(
                postCreateRequestDTO.userId(),
                postCreateRequestDTO.title(),
                postCreateRequestDTO.description(),
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
    public ResponseEntity<ResponseDTO<CategoryMonoListResponseDTO>> getAllCategories() {
        CategoryMonoListResponseDTO categoryMonoListResponseDTO = postGetCategoriesUseCase.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(categoryMonoListResponseDTO));
    }

    @GetMapping("/categories/food")
    public ResponseEntity<ResponseDTO<CategoryMonoListResponseDTO>> getFoodCategories() {
        CategoryMonoListResponseDTO categoryMonoListResponseDTO = postGetCategoriesUseCase.getFoodCategories();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(categoryMonoListResponseDTO));
    }

    @PostMapping("/scoop")
    public ResponseEntity<ResponseDTO<Void>> scoopPost(@RequestBody ScoopPostRequestDTO scoopPostRequestDTO) {
        PostScoopPostCommand command = new PostScoopPostCommand(scoopPostRequestDTO.userId(), scoopPostRequestDTO.postId());
        postScoopPostUseCase.scoopPost(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
