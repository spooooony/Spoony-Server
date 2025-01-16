package com.spoony.spoony_server.domain.post.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.post.dto.PostCreateDTO;
import com.spoony.spoony_server.domain.post.dto.request.PostCreateRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.PostResponseDTO;
import com.spoony.spoony_server.domain.post.service.PostService;
import com.spoony.spoony_server.infra.service.AwsFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AwsFileService awsFileService;

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<PostResponseDTO>> getPost(@PathVariable Long postId) {
        PostResponseDTO postResponse = postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(postResponse));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<Void>> createPost(
            @RequestPart("data") PostCreateRequestDTO postCreateRequestDTO,
            @RequestPart("photos") List<MultipartFile> photos
    ) throws IOException {
        List<String> photoUrlList = awsFileService.savePostImages(photos);

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

        postService.createPost(updatedPostCreateDTO);

        return ResponseEntity.ok(ResponseDTO.success(null));
    }
}
