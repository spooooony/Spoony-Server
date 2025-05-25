package com.spoony.spoony_server.adapter.dto.user.response;

import com.spoony.spoony_server.adapter.dto.post.response.RegionDTO;

import java.util.List;

public record RegionListResponseDTO(List<RegionDTO> regionList) {
}
