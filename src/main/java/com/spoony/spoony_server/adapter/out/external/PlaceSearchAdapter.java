package com.spoony.spoony_server.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.adapter.dto.place.PlaceListResponseDTO;
import com.spoony.spoony_server.adapter.dto.place.PlaceResponseDTO;
import com.spoony.spoony_server.application.port.out.place.PlaceSearchPort;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PlaceErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class PlaceSearchAdapter implements PlaceSearchPort {

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
}
