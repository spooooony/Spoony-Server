package com.spoony.spoony_server.adapter.in.web.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/ex")
public class ExampleController {

    @GetMapping
    @Operation(summary = "전체 게시글 목록 조회", description = "전체 게시글을 페이징으로 조회합니다.")
    public ResponseEntity<Object> getAllPosts(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("전체 게시글 목록 예시입니다.");
    }
}