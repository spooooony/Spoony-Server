package com.spoony.spoony_server.application.port.out.post;

import java.util.List;

public interface PostDeletePort {
    void deleteImagesFromS3(List<String> imageUrls);
}
