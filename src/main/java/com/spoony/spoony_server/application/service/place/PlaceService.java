package com.spoony.spoony_server.application.service.place;

import com.spoony.spoony_server.application.port.command.place.PlaceCheckCommand;
import com.spoony.spoony_server.application.port.command.place.PlaceGetCommand;
import com.spoony.spoony_server.application.port.out.place.PlaceSearchPort;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.adapter.dto.place.PlaceCheckRequestDTO;
import com.spoony.spoony_server.adapter.dto.place.PlaceListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.spoony.spoony_server.application.port.in.place.PlaceDuplicateCheckUseCase;
import com.spoony.spoony_server.application.port.in.place.PlaceSearchUseCase;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService implements
        PlaceSearchUseCase,
        PlaceDuplicateCheckUseCase {

    private final UserRepository userRepository;
    public final PlaceRepository placeRepository;
    public final PostRepository postRepository;

    public final PlaceSearchPort placeSearchPort;

    @Value("${naver.clientId}")
    private String clientId;

    @Value("${naver.clientSecret}")
    private String clientSecret;

    public PlaceListResponseDTO getPlaceList(PlaceGetCommand command) {
        return placeSearchPort.getPlaceList(command.getQuery(), command.getDisplay());
    }

    public Boolean isDuplicate(PlaceCheckCommand command) {

        List<PostEntity> userPosts = postRepository.findByUser(userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        );

        List<Long> placeIds = userPosts.stream()
                .map(post -> post.getPlace().getPlaceId())
                .toList();

        boolean exists = placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, command.getLatitude(), command.getLongitude());

        return exists;
    }
}

