package com.spoony.spoony_server.application.service.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PlaceErrorMessage;
import com.spoony.spoony_server.application.port.dto.place.PlaceCheckRequestDTO;
import com.spoony.spoony_server.application.port.dto.place.PlaceListResponseDTO;
import com.spoony.spoony_server.application.port.dto.place.PlaceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;
import com.spoony.spoony_server.application.port.in.place.PlaceDuplicateCheckUseCase;
import com.spoony.spoony_server.application.port.in.place.PlaceSearchUseCase;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.place.jpa.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PlaceService implements
        PlaceSearchUseCase,
        PlaceDuplicateCheckUseCase {

    private final UserRepository userRepository;
    public final PlaceRepository placeRepository;
    public final PostRepository postRepository;

    @Value("${naver.clientId}")
    private String clientId;

    @Value("${naver.clientSecret}")
    private String clientSecret;

    public PlaceListResponseDTO getPlaceList(String query, int display) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", 1)
                .queryParam("sort", "comment")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        ResponseEntity<String> searchResult = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();

        String searchResultBody = searchResult.getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode placeList = objectMapper.readTree(searchResultBody);
            List<PlaceResponseDTO> places = new ArrayList<>();

            placeList.get("items").forEach(item -> {
                String title = HtmlUtils.htmlUnescape(
                        item.get("title").asText().replaceAll("<[^>]*>", ""));
                String address = HtmlUtils.htmlUnescape(
                        item.get("address").asText().replaceAll("<[^>]*>", ""));
                String roadAddress = HtmlUtils.htmlUnescape(
                        item.get("roadAddress").asText().replaceAll("<[^>]*>", ""));
                Double mapx = item.get("mapx").asDouble();
                Double mapy = item.get("mapy").asDouble();

                places.add(new PlaceResponseDTO(title, address, roadAddress, mapy / 10000000, mapx / 10000000));
            });

            return new PlaceListResponseDTO(places);

        } catch (Exception e) {
            throw new BusinessException(PlaceErrorMessage.JSON_PARSE_ERROR);
        }
    }

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

