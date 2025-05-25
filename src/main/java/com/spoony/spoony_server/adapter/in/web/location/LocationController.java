package com.spoony.spoony_server.adapter.in.web.location;

import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.in.location.LocationSearchUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.adapter.dto.location.response.LocationResponseListDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "지역 검색 API", description = "검색어를 통해 지역(구, 동, 역)을 검색하는 API")
    public ResponseEntity<ResponseDTO<LocationResponseListDTO>> searchLocations(
            @RequestParam String query) {
        LocationSearchCommand command = new LocationSearchCommand(query);
        LocationResponseListDTO locationResponseListDTO = locationSearchUseCase.searchLocationsByQuery(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(locationResponseListDTO));
    }
}
