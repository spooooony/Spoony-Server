package com.spoony.spoony_server.adapter.in.web.spoon;

import com.spoony.spoony_server.adapter.dto.spoon.response.SpoonDrawResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.response.SpoonDrawListResponseDTO;
import com.spoony.spoony_server.application.port.command.spoon.SpoonDrawCommand;
import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.in.spoon.SpoonDrawUseCase;
import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.response.SpoonResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spoon")
public class SpoonController {

    private final SpoonGetUseCase spoonGetUseCase;
    private final SpoonDrawUseCase spoonDrawUseCase;

    @GetMapping
    @Operation(summary = "스푼 개수 조회 API", description = "특정 사용자의 스푼 개수를 조회합니다.")
    public ResponseEntity<ResponseDTO<SpoonResponseDTO>> getSpoonBalance(
            @UserId Long userId) {
        SpoonGetCommand command = new SpoonGetCommand(userId);
        SpoonResponseDTO spoonResponseDTO = spoonGetUseCase.getAmountById(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(spoonResponseDTO));
    }

    @PostMapping("/draw")
    @Operation(summary = "스푼 뽑기 API", description = "스푼 뽑기를 진행합니다.")
    public ResponseEntity<ResponseDTO<SpoonDrawResponseDTO>> createSpoonDraw(
            @UserId Long userId) {
        SpoonDrawCommand command = new SpoonDrawCommand(userId);
        SpoonDrawResponseDTO spoonDrawResponseDTO = spoonDrawUseCase.createDrawById(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(spoonDrawResponseDTO));
    }

    @GetMapping("/draw")
    @Operation(summary = "스푼 뽑기 기록 조회 API", description = "스푼 뽑기 주간 기록을 조회합니다.")
    public ResponseEntity<ResponseDTO<SpoonDrawListResponseDTO>> getWeeklySpoonDraw(
            @UserId Long userId) {
        SpoonDrawCommand command = new SpoonDrawCommand(userId);
        SpoonDrawListResponseDTO spoonDrawListResponseDTO = spoonDrawUseCase.getDrawById(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(spoonDrawListResponseDTO));
    }
}
