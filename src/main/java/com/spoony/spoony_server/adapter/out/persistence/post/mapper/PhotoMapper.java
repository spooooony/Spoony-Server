package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PhotoEntity;
import com.spoony.spoony_server.domain.post.Photo;

public class PhotoMapper {

    public static Photo toDomain(PhotoEntity photoEntity) {
        return new Photo(
                photoEntity.getPhotoId(),
                PostMapper.toDomain(photoEntity.getPost()),
                photoEntity.getPhotoUrl()
        );
    }
}
