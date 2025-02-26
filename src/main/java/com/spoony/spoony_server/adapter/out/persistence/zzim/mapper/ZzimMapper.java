package com.spoony.spoony_server.adapter.out.persistence.zzim.mapper;

import com.spoony.spoony_server.adapter.out.persistence.place.mapper.PlaceMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.domain.zzim.ZzimPost;

public class ZzimMapper {

    public static ZzimPost toDomain(ZzimPostEntity zzimPostEntity){
        return new ZzimPost(
                zzimPostEntity.getZzimId(),
                new User(
                        zzimPostEntity.getUser().getUserId(),
                        zzimPostEntity.getUser().getProvider(),
                        zzimPostEntity.getUser().getProviderId(),
                        zzimPostEntity.getUser().getUserName(),
                        zzimPostEntity.getUser().getUserImage(),
                        RegionMapper.toDomain(zzimPostEntity.getUser().getRegion()),
                        zzimPostEntity.getUser().getCreatedAt(),
                        zzimPostEntity.getUser().getUpdatedAt()
             ),
                new Post(
                        zzimPostEntity.getPost().getPostId(),
                        UserMapper.toDomain(zzimPostEntity.getPost().getUser()), // `getUser()`로 수정
                        PlaceMapper.toDomain(zzimPostEntity.getPost().getPlace()), // `Place` 객체 매핑
                        zzimPostEntity.getPost().getTitle(),
                        zzimPostEntity.getPost().getDescription(),
                        zzimPostEntity.getPost().getCreatedAt(),
                        zzimPostEntity.getPost().getUpdatedAt()
                )
        );
    }
}
