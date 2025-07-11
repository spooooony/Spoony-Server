package com.spoony.spoony_server.adapter.in.web.zzim;

import com.spoony.spoony_server.application.port.command.zzim.*;
import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimDeleteUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.adapter.dto.zzim.response.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.response.ZzimFocusListResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/zzim")
public class ZzimPostController {

    private final ZzimAddUseCase zzimAddUseCase;
    private final ZzimGetUseCase zzimGetUseCase;
    private final ZzimDeleteUseCase zzimRemoveUseCase;

    @PostMapping
    @Operation(summary = "북마크 추가 API", description = "북마크에 새로운 게시물을 추가합니다.")
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(
            @UserId Long userId,
            @RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        ZzimAddCommand command = new ZzimAddCommand(
                userId,
                zzimPostAddRequestDTO.postId()
        );
        zzimAddUseCase.addZzimPost(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @GetMapping
    @Operation(summary = "북마크 조회 API", description = "북마크 장소 리스트를 조회합니다.")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimCardList(
            @UserId Long userId,
            @RequestParam(defaultValue = "1") Long categoryId)
    {
        ZzimGetCardCommand command = new ZzimGetCardCommand(userId,categoryId);
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimCardList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @GetMapping("/{placeId}")
    @Operation(summary = "특정 장소의 북마크 리스트 조회 API", description = "특정 장소의 북마크 장소 리스트를 조회합니다.")
    public ResponseEntity<ResponseDTO<ZzimFocusListResponseDTO>> getZzimFocusList(
            @UserId Long userId,
            @PathVariable long placeId) {
        ZzimGetFocusCommand command = new ZzimGetFocusCommand(userId, placeId);
        ZzimFocusListResponseDTO zzimFocusListResponse = zzimGetUseCase.getZzimFocusList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimFocusListResponse));
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "특정 지역의 북마크 조회 API", description = "특정 지역의 북마크 장소 리스트를 조회합니다.")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimLocationCardList(
            @UserId Long userId,
            @PathVariable long locationId) {
        ZzimGetLocationCardCommand command = new ZzimGetLocationCardCommand(userId, locationId);
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimByLocation(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "북마크 삭제 API", description = "북마크에서 특정 게시물을 삭제합니다.")
    public ResponseEntity<ResponseDTO<Void>> deleteZzim(
            @UserId Long userId,
            @PathVariable long postId) {
        ZzimDeleteCommand command = new ZzimDeleteCommand(userId, postId);
        zzimRemoveUseCase.deleteZzim(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
