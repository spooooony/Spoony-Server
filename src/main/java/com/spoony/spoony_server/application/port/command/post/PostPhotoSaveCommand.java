package com.spoony.spoony_server.application.port.command.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PostPhotoSaveCommand {
    private final List<MultipartFile> photos;
}
