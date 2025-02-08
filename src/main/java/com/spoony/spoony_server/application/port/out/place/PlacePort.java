package com.spoony.spoony_server.application.port.out.place;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.post.Post;

import java.util.List;

public interface PlacePort {

    List<Post> findPostsByUser(Long userId);

    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude);
}
