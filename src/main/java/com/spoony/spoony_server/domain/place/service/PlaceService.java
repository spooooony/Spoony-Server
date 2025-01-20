package com.spoony.spoony_server.domain.place.service;

import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.dto.request.PlaceCheckRequestDTO;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final UserRepository userRepository;
    public final PlaceRepository placeRepository;
    public final PostRepository postRepository;

    public Boolean isDuplicate(PlaceCheckRequestDTO placeCheckRequestDTO) {
        Long userId = placeCheckRequestDTO.userId();
        Double latitude = placeCheckRequestDTO.latitude();
        Double longitude = placeCheckRequestDTO.longitude();

        List<PostEntity> userPosts = postRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        );

        List<Long> placeIds = userPosts.stream()
                .map(post -> post.getPlace().getPlaceId())
                .toList();

        boolean exists = placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, latitude, longitude);

        return exists;
    }
}

