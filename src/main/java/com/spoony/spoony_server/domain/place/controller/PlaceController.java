package com.spoony.spoony_server.domain.place.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PlaceErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @GetMapping(value = "/search")
    public ResponseEntity<ResponseDTO<JsonNode>> getPlaceList(
            @RequestParam(name = "query") String text,
            @RequestParam(name = "display", required = false, defaultValue = "5") int display) {

        // 네이버 지역 검색 API
        String clientId = "RftWm05kMhRNzohzXoIO";
        String clientSecret = "6GAKpX5obG";

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", text)
                .queryParam("display", display)
                .queryParam("start", 1)
                .queryParam("sort", "comment")
                .encode(Charset.forName("UTF-8"))
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

            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(placeList));

        } catch (Exception e) {
            throw new BusinessException(PlaceErrorMessage.JSON_PARSE_ERROR);
        }
    }
}
