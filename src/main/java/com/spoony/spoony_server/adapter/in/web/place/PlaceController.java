package com.spoony.spoony_server.adapter.in.web.place;

import com.spoony.spoony_server.application.port.in.place.PlaceDuplicateCheckUseCase;
import com.spoony.spoony_server.application.port.in.place.PlaceSearchUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.place.PlaceCheckRequestDTO;
import com.spoony.spoony_server.application.port.dto.place.PlaceCheckResponseDTO;
import com.spoony.spoony_server.application.port.dto.place.PlaceListResponseDTO;
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
    public ResponseEntity<ResponseDTO<PlaceListResponseDTO>> getPlaceList(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "display", required = false, defaultValue = "5") int display) {
        PlaceListResponseDTO placeListResponseDTO = placeSearchUseCase.getPlaceList(query, display);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(placeListResponseDTO));
    }

    @PostMapping("/check")
    public ResponseEntity<ResponseDTO<PlaceCheckResponseDTO>> checkDuplicatePlace(
            @RequestBody PlaceCheckRequestDTO placeCheckRequestDTO) {
        boolean isDuplicate = placeDuplicateCheckUseCase.isDuplicate(placeCheckRequestDTO);
        PlaceCheckResponseDTO placeCheckResponseDTO = new PlaceCheckResponseDTO(isDuplicate);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(placeCheckResponseDTO));
    }
}
