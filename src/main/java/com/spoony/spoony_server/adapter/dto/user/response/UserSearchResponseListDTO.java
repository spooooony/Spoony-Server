package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record UserSearchResponseListDTO(List<UserSimpleResponseDTO> userSimpleResponseDTO) {

    public static UserSearchResponseListDTO of(List<UserSimpleResponseDTO> userSimpleResponseDTO) {
        return new UserSearchResponseListDTO(userSimpleResponseDTO);
    }
}
