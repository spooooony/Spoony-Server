package com.spoony.spoony_server.adapter.dto.user;

import com.spoony.spoony_server.adapter.dto.location.LocationResponseDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationTypeDTO;

import java.util.List;

public record UserSearchResultListDTO(List<UserSearchResultDTO> userSimpleResponseDTO) {
}

