package com.spoony.spoony_server.adapter.dto.post;

import com.spoony.spoony_server.adapter.dto.user.UserSearchResultDTO;

import java.util.List;

public record PostSearchResultListDTO(List<FeedResponseDTO> postSearchResultList) {
}
