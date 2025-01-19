package com.spoony.spoony_server.domain.location.controller;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.domain.location.dto.response.LocationResponseListDTO;
import com.spoony.spoony_server.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<LocationResponseListDTO>> searchLocations(@RequestParam String query) {
        LocationResponseListDTO locationResponseListDTO = locationService.searchLocationsByQuery(query);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(locationResponseListDTO));
    }
}
