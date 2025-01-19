package com.spoony.spoony_server.domain.post.dto.response;

import java.util.List;


//public record ZzimCardResponseDTO(Long placeId,
//                                  String placeName,
//                                  String placeAddress,
//                                  String postTitle,
//                                  Double latitude,
//                                  Double longitude,
//                                  CategoryColorResponseDTO categoryColorResponse) {
//}
//


public record PostResponseDTO(Long postId, Long userId,
                              List<String> photoUrlList,
                              String title,
                              String date,
                              List<String> menuList,
                              String description,
                              String placeName, String placeAddress, Double latitude,
                              Double longitude, Long zzinCount, Boolean isZzim, Boolean isScoop,
                              CategoryColorResponseDTO categoryColorResponse
) {
}


//Integer postId, String title, String description, LocalDateTime createdAt, LocalDateTime updatedAt