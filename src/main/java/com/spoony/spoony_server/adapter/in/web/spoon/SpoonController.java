package com.spoony.spoony_server.adapter.in.web.spoon;

import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.spoon.SpoonResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spoon")
public class SpoonController {

    private final SpoonGetUseCase spoonGetUseCase;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<SpoonResponseDTO>> getSpoonBalance(@PathVariable Long userId) {
        SpoonResponseDTO spoonResponseDTO = spoonGetUseCase.getAmountById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(spoonResponseDTO));
    }
}
