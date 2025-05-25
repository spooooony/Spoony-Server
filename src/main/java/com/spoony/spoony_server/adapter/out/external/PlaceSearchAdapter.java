package com.spoony.spoony_server.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.adapter.dto.place.response.PlaceListResponseDTO;
import com.spoony.spoony_server.adapter.dto.place.response.PlaceResponseDTO;
import com.spoony.spoony_server.application.port.out.place.PlaceSearchPort;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PlaceErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class PlaceSearchAdapter implements PlaceSearchPort {

    private final NaverFeignClient naverPlaceFeignClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${naver.clientId}")
    private String clientId;

    @Value("${naver.clientSecret}")
    private String clientSecret;

    public PlaceListResponseDTO getPlaceList(String query, int display) {
        String searchResultBody = naverPlaceFeignClient.searchPlaces(clientId, clientSecret, query, display, 1, "comment");

        try {
            JsonNode placeList = objectMapper.readTree(searchResultBody);
            List<PlaceResponseDTO> places = new ArrayList<>();

            placeList.get("items").forEach(item -> {
                String title = HtmlUtils.htmlUnescape(item.get("title").asText().replaceAll("<[^>]*>", ""));
                String address = HtmlUtils.htmlUnescape(item.get("address").asText().replaceAll("<[^>]*>", ""));
                String roadAddress = HtmlUtils.htmlUnescape(item.get("roadAddress").asText().replaceAll("<[^>]*>", ""));
                double mapx = item.get("mapx").asDouble();
                double mapy = item.get("mapy").asDouble();

                if (!address.contains("서울")) return;

                places.add(PlaceResponseDTO.of(title, address, roadAddress, mapy / 10000000, mapx / 10000000));
            });

            return PlaceListResponseDTO.of(places);

        } catch (Exception e) {
            throw new BusinessException(PlaceErrorMessage.JSON_PARSE_ERROR);
        }
    }
}
