package com.spoony.spoony_server.domain.post.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.ZzimCardListResponse;
import com.spoony.spoony_server.domain.post.dto.response.ZzimFocusListResponse;
import com.spoony.spoony_server.domain.post.service.PostService;
import com.spoony.spoony_server.domain.post.service.ZzimPostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/zzim")
@RequiredArgsConstructor
public class ZzimPostController {

    public final ZzimPostService zzimPostService;
    
    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(@RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        zzimPostService.addZzimPost(zzimPostAddRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponse>> getZzimCardList(@PathVariable Long userId) {
        ZzimCardListResponse zzimCardListResponse = zzimPostService.getZzimCardList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @GetMapping("/{userId}/{placeId}")
    public ResponseEntity<ResponseDTO<ZzimFocusListResponse>> getZzimFocusList(@PathVariable Long userId, @PathVariable Long placeId) {
        ZzimFocusListResponse zzimFocusListResponse = zzimPostService.getZzimFocusList(userId, placeId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimFocusListResponse));
    }
}
