package com.spoony.spoony_server.adapter.in.web.location;

import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.application.port.dto.location.LocationResponseListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationSearchUseCase locationSearchUseCase;

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<LocationResponseListDTO>> searchLocations(@RequestParam String query) {
        LocationResponseListDTO locationResponseListDTO = locationSearchUseCase.searchLocationsByQuery(query);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(locationResponseListDTO));
    }
}
