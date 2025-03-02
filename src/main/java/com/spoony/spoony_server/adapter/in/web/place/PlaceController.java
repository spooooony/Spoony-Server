package com.spoony.spoony_server.adapter.in.web.place;

import com.spoony.spoony_server.application.port.command.place.PlaceCheckCommand;
import com.spoony.spoony_server.application.port.command.place.PlaceGetCommand;
import com.spoony.spoony_server.application.port.in.place.PlaceDuplicateCheckUseCase;
import com.spoony.spoony_server.application.port.in.place.PlaceSearchUseCase;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.place.PlaceCheckRequestDTO;
import com.spoony.spoony_server.adapter.dto.place.PlaceCheckResponseDTO;
import com.spoony.spoony_server.adapter.dto.place.PlaceListResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/place")
public class PlaceController {

    private final PlaceSearchUseCase placeSearchUseCase;
    private final PlaceDuplicateCheckUseCase placeDuplicateCheckUseCase;

    @GetMapping(value = "/search")
    @Operation(summary = "장소 검색 API", description = "검색어를 통해 특정 음식점을 검색하는 API")
    public ResponseEntity<ResponseDTO<PlaceListResponseDTO>> getPlaceList(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "display", required = false, defaultValue = "5") int display) {
        PlaceGetCommand command = new PlaceGetCommand(query, display);
        PlaceListResponseDTO placeListResponseDTO = placeSearchUseCase.getPlaceList(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(placeListResponseDTO));
    }

    @PostMapping("/check")
    @Operation(summary = "장소 중복 확인 API", description = "중복 등록된 장소가 존재하는지 확인하는 API")
    public ResponseEntity<ResponseDTO<PlaceCheckResponseDTO>> checkDuplicatePlace(
            @UserId Long userId,
            @RequestBody PlaceCheckRequestDTO placeCheckRequestDTO) {
        PlaceCheckCommand command = new PlaceCheckCommand(
                userId,
                placeCheckRequestDTO.latitude(),
                placeCheckRequestDTO.longitude()
        );
        boolean isDuplicate = placeDuplicateCheckUseCase.isDuplicate(command);
        PlaceCheckResponseDTO placeCheckResponseDTO = new PlaceCheckResponseDTO(isDuplicate);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(placeCheckResponseDTO));
    }
}
