package com.spoony.spoony_server.domain.post.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.ZzimCardListResponse;
import com.spoony.spoony_server.domain.post.service.PostService;
import com.spoony.spoony_server.domain.post.service.ZzimPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/zzim")
@RequiredArgsConstructor
public class ZzimPostController {

    public final ZzimPostService zzimPostService;
    private final PostService postService;
    
    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(@RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        zzimPostService.addZzimPost(zzimPostAddRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @GetMapping("/zzim/{userId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponse>> getZzimCardList(@PathVariable Long userId) {
        ZzimCardListResponse zzimCardListResponse = postService.getZzimCardList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }
}