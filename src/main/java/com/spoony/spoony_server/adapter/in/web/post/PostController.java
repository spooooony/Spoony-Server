package com.spoony.spoony_server.adapter.in.web.post;

import com.spoony.spoony_server.application.port.in.post.PostCreateUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetCategoriesUseCase;
import com.spoony.spoony_server.application.port.in.post.PostGetUseCase;
import com.spoony.spoony_server.application.port.in.post.PostScoopPostUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.post.PostCreateDTO;
import com.spoony.spoony_server.application.port.dto.post.CategoryMonoListResponseDTO;
import com.spoony.spoony_server.application.port.dto.post.PostResponseDTO;
import com.spoony.spoony_server.application.port.dto.spoon.ScoopPostRequestDTO;
import com.spoony.spoony_server.application.port.dto.zzim.PostCreateRequestDTO;
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

    @GetMapping("/{userId}/{postId}")
    public ResponseEntity<ResponseDTO<PostResponseDTO>> getPost(@PathVariable Long postId, @PathVariable Long userId) {
        PostResponseDTO postResponse = postGetUseCase.getPostById(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postResponse));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<Void>> createPost(
            @RequestPart("data") PostCreateRequestDTO postCreateRequestDTO,
            @RequestPart("photos") List<MultipartFile> photos
    ) throws IOException {
        List<String> photoUrlList = postCreateUseCase.savePostImages(photos);

        PostCreateDTO updatedPostCreateDTO = new PostCreateDTO(
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

        postCreateUseCase.createPost(updatedPostCreateDTO);

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
        postScoopPostUseCase.scoopPost(scoopPostRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
