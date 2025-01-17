package com.spoony.spoony_server.domain.spoon.controller;


import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.spoon.dto.request.ScoopPostRequestDTO;
import com.spoony.spoony_server.domain.spoon.service.ScoopPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/scoop")
public class ScoopPostController {
    public final ScoopPostService scoopPostService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> ScoopPost(@RequestBody ScoopPostRequestDTO scoopPostRequestDTO) {
        scoopPostService.ScoopPost(scoopPostRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));

    }


}
