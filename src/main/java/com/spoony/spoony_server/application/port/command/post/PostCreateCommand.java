package com.spoony.spoony_server.application.port.command.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PostCreateCommand {
    private final Long userId;
    private final String description;
    private final Double value;
    private final String cons;
    private final String placeName;
    private final String placeAddress;
    private final String placeRoadAddress;
    private final Double latitude;
    private final Double longitude;
    private final Long categoryId;
    private final List<String> menuList;
    private final List<String> photoUrlList;
}
