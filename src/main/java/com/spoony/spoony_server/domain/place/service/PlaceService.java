package com.spoony.spoony_server.domain.place.service;

import com.spoony.spoony_server.domain.place.dto.request.PlaceCheckRequestDTO;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    public final PlaceRepository placeRepository;
    public final PostRepository postRepository;

    public PlaceService(PlaceRepository placeRepository, PostRepository postRepository) {
        this.placeRepository = placeRepository;
        this.postRepository = postRepository;
    }

    public Boolean isDuplicate(PlaceCheckRequestDTO placeCheckRequestDTO) {
        Long userId = placeCheckRequestDTO.userId();
        Double latitude = placeCheckRequestDTO.latitude();
        Double longitude = placeCheckRequestDTO.longitude();

        List<PostEntity> userPosts = postRepository.findByUser_UserId(userId);

        List<Integer> placeIds = userPosts.stream()
                .map(post -> post.getPlace().getPlaceId())
                .toList();

        boolean exists = placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, latitude, longitude);

        return exists;
    }
}

