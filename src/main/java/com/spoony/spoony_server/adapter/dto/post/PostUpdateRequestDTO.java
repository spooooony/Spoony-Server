package com.spoony.spoony_server.adapter.dto.post;

import java.util.List;

public record PostUpdateRequestDTO(Long postId,
                                   String description,
                                   Double value,
                                   String cons,
                                   Long categoryId,
                                   List<String> menuList,
                                   List<String> deleteImageUrlList) {
}
