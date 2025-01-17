package com.spoony.spoony_server.domain.post.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.service.ZzimPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post/zzim")
public class ZzimPostController {
    public final ZzimPostService zzimPostService;

    public ZzimPostController(ZzimPostService zzimPostService) {
        this.zzimPostService = zzimPostService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(@RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        zzimPostService.addZzimPost(zzimPostAddRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));


    }
}