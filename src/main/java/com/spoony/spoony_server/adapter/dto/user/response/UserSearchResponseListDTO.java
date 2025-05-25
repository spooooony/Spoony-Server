package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record UserSearchResponseListDTO(List<UserSearchResponseDTO> userSimpleResponseDTO) {

    public static UserSearchResponseListDTO of(List<UserSearchResponseDTO> userSimpleResponseDTO) {
        return new UserSearchResponseListDTO(userSimpleResponseDTO);
    }
}
