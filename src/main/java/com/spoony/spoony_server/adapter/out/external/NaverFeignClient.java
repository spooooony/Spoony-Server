package com.spoony.spoony_server.adapter.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverPlaceSearch", url = "https://openapi.naver.com/v1/search")
public interface NaverFeignClient {
    @GetMapping(value = "/local.json", consumes = MediaType.APPLICATION_JSON_VALUE)
    String searchPlaces(
            @RequestHeader("X-Naver-Client-Id") String clientId,
            @RequestHeader("X-Naver-Client-Secret") String clientSecret,
            @RequestParam("query") String query,
            @RequestParam("display") int display,
            @RequestParam("start") int start,
            @RequestParam("sort") String sort
    );
}
