package com.spoony.spoony_server.adapter.out.persistence.zzim.adapter;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.jpa.ZzimPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.jpa.ZzimPostRepository;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ZzimPersistenceAdapter implements ZzimPort {

    private final ZzimPostRepository zzimPostRepository;

    @Override
    public Long countByPost(PostEntity postEntity) {
        return zzimPostRepository.countByPost(postEntity);
    }

    @Override
    public boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity) {
        return zzimPostRepository.existsByUserAndPost(userEntity, postEntity);
    }

    @Override
    public List<ZzimPostEntity> findByUser(UserEntity userEntity) {
        return zzimPostRepository.findByUser(userEntity);
    }

    @Override
    public void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity) {
        zzimPostRepository.deleteByUserAndPost(userEntity, postEntity);
    }
}
