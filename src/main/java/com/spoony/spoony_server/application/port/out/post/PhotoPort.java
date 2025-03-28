package com.spoony.spoony_server.application.port.out.post;

import java.util.List;

public interface PhotoPort {
    List<String> getPhotoUrls(Long postId);
}
