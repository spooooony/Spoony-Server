package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record ProfileImageListResponseDTO(List<ProfileImageResponseDTO> images) {

    public static ProfileImageListResponseDTO of(List<ProfileImageResponseDTO> images) {
        return new ProfileImageListResponseDTO(images);
    }
}
