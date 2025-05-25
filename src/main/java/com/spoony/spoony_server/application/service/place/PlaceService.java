package com.spoony.spoony_server.application.service.place;

import com.spoony.spoony_server.application.port.command.place.PlaceCheckCommand;
import com.spoony.spoony_server.application.port.command.place.PlaceGetCommand;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.application.port.out.place.PlaceSearchPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.adapter.dto.place.response.PlaceListResponseDTO;
import lombok.RequiredArgsConstructor;
import com.spoony.spoony_server.application.port.in.place.PlaceDuplicateCheckUseCase;
import com.spoony.spoony_server.application.port.in.place.PlaceSearchUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService implements
        PlaceSearchUseCase,
        PlaceDuplicateCheckUseCase {

    public final PlaceSearchPort placeSearchPort;
    public final PlacePort placePort;
    public final PostPort postPort;

    public PlaceListResponseDTO getPlaceList(PlaceGetCommand command) {
        return placeSearchPort.getPlaceList(command.getQuery(), command.getDisplay());
    }

    public Boolean isDuplicate(PlaceCheckCommand command) {
        List<Post> userPosts = postPort.findPostsByUserId(command.getUserId());

        List<Long> placeIds = userPosts.stream()
                .map(post -> post.getPlace().getPlaceId())
                .toList();

        boolean exists = placePort.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, command.getLatitude(), command.getLongitude());

        return exists;
    }
}
