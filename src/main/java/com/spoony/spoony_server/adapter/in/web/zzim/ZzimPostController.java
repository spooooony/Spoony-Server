package com.spoony.spoony_server.adapter.in.web.zzim;

import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardListWithCursorResponseDTO;
import com.spoony.spoony_server.application.port.command.zzim.*;
import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimDeleteUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimFocusListResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/zzim")
public class ZzimPostController {

    private final ZzimAddUseCase zzimAddUseCase;
    private final ZzimGetUseCase zzimGetUseCase;
    private final ZzimDeleteUseCase zzimRemoveUseCase;

    @PostMapping
    @Operation(summary = "북마크 추가 API", description = "북마크에 새로운 게시물을 추가하는 API")
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
    @Operation(summary = "북마크 조회 API", description = "북마크 장소 리스트를 조회하는 API")
    public ResponseEntity<ResponseDTO<ZzimCardListWithCursorResponseDTO >> getZzimCardList(
            @UserId Long userId,
            @RequestParam(defaultValue = "1") Long categoryId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        ZzimGetCardCommand command = new ZzimGetCardCommand(userId,categoryId, cursor, size);
        ZzimCardListWithCursorResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimCardList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @GetMapping("/{placeId}")
    @Operation(summary = "특정 장소의 북마크 리스트 조회 API", description = "특정 장소의 북마크 장소 리스트를 조회하는 API")
    public ResponseEntity<ResponseDTO<ZzimFocusListResponseDTO>> getZzimFocusList(
            @UserId Long userId,
            @PathVariable long placeId) {
        ZzimGetFocusCommand command = new ZzimGetFocusCommand(userId, placeId);
        ZzimFocusListResponseDTO zzimFocusListResponse = zzimGetUseCase.getZzimFocusList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimFocusListResponse));
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "특정 지역의 북마크 조회 API", description = "특정 지역의 북마크 장소 리스트를 조회하는 API")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimLocationCardList(
            @UserId Long userId,
            @PathVariable long locationId) {
        ZzimGetLocationCardCommand command = new ZzimGetLocationCardCommand(userId, locationId);
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimByLocation(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "북마크 삭제 API", description = "북마크에서 특정 게시물을 삭제하는 API")
    public ResponseEntity<ResponseDTO<Void>> deleteZzim(
            @UserId Long userId,
            @PathVariable long postId) {
        ZzimDeleteCommand command = new ZzimDeleteCommand(userId, postId);
        zzimRemoveUseCase.deleteZzim(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
