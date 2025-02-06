package com.spoony.spoony_server.adapter.in.web.zzim;

import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimRemoveUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.zzim.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.application.port.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.application.port.dto.zzim.ZzimFocusListResponseDTO;
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
    private final ZzimRemoveUseCase zzimRemoveUseCase;

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(@RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        zzimAddUseCase.addZzimPost(zzimPostAddRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimCardList(@PathVariable Long userId) {
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimCardList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @GetMapping("/{userId}/{placeId}")
    public ResponseEntity<ResponseDTO<ZzimFocusListResponseDTO>> getZzimFocusList(@PathVariable Long userId, @PathVariable Long placeId) {
        ZzimFocusListResponseDTO zzimFocusListResponse = zzimGetUseCase.getZzimFocusList(userId, placeId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimFocusListResponse));
    }

    @GetMapping("/location/{userId}/{locationId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimCardList(@PathVariable Long userId, @PathVariable Long locationId) {
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimByLocation(userId, locationId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @DeleteMapping("/{userId}/{placeId}")
    public ResponseEntity<ResponseDTO<Void>> deleteZzim(@PathVariable Long userId, @PathVariable Long placeId) {
        zzimRemoveUseCase.deleteZzim(userId, placeId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
