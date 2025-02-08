package com.spoony.spoony_server.adapter.dto.post;

import java.util.List;

public record PostCreateDTO(long userId,
                            String title,
                            String description,
                            String placeName,
                            String placeAddress,
                            String placeRoadAddress,
                            Double latitude,
                            Double longitude,
                            long categoryId,
                            List<String> menuList,
                            List<String> photoUrlList) {
}
