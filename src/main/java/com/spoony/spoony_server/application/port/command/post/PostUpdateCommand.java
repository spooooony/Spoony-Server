package com.spoony.spoony_server.application.port.command.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PostUpdateCommand {
    private final Long postId;
    private final String description;
    private final Double value;
    private final String cons;
    private final Long categoryId;
    private final List<String> menuList;
    private final List<String> photoUrlList;
    private final List<String> deletePhotoUrlList;
}
