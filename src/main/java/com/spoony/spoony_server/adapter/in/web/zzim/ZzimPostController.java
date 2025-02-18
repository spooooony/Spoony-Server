package com.spoony.spoony_server.adapter.in.web.zzim;

import com.spoony.spoony_server.application.port.command.zzim.*;
import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimDeleteUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimFocusListResponseDTO;
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
    public ResponseEntity<ResponseDTO<Void>> addZzimPost(@RequestBody ZzimPostAddRequestDTO zzimPostAddRequestDTO) {
        ZzimAddCommand command = new ZzimAddCommand(
                zzimPostAddRequestDTO.userId(),
                zzimPostAddRequestDTO.postId()
        );
        zzimAddUseCase.addZzimPost(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimCardList(@PathVariable long userId) {
        ZzimGetCardCommand command = new ZzimGetCardCommand(userId);
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimCardList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @GetMapping("/{userId}/{placeId}")
    public ResponseEntity<ResponseDTO<ZzimFocusListResponseDTO>> getZzimFocusList(@PathVariable long userId, @PathVariable long placeId) {
        ZzimGetFocusCommand command = new ZzimGetFocusCommand(userId, placeId);
        ZzimFocusListResponseDTO zzimFocusListResponse = zzimGetUseCase.getZzimFocusList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimFocusListResponse));
    }

    @GetMapping("/location/{userId}/{locationId}")
    public ResponseEntity<ResponseDTO<ZzimCardListResponseDTO>> getZzimLocationCardList(@PathVariable long userId, @PathVariable long locationId) {
        ZzimGetLocationCardCommand command = new ZzimGetLocationCardCommand(userId, locationId);
        ZzimCardListResponseDTO zzimCardListResponse = zzimGetUseCase.getZzimByLocation(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(zzimCardListResponse));
    }

    @DeleteMapping("/{userId}/{postId}")
    public ResponseEntity<ResponseDTO<Void>> deleteZzim(@PathVariable long userId, @PathVariable long postId) {
        ZzimDeleteCommand command = new ZzimDeleteCommand(userId, postId);
        zzimRemoveUseCase.deleteZzim(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}
