package com.spoony.spoony_server.domain.spoon.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.spoon.dto.response.SpoonResponseDTO;
import com.spoony.spoony_server.domain.spoon.service.SpoonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spoon")
public class SpoonController {

    private final SpoonService spoonService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<SpoonResponseDTO>> getSpoonBalance(@PathVariable Long userId) {
        SpoonResponseDTO spoonResponseDTO = spoonService.getAmountById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(spoonResponseDTO));
    }
}
