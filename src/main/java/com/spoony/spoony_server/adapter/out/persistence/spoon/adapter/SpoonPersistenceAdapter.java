package com.spoony.spoony_server.adapter.out.persistence.spoon.adapter;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.jpa.*;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SpoonPersistenceAdapter implements SpoonPort {

    private final SpoonBalanceRepository balanceRepository;
    private final SpoonHistoryRepository historyRepository;
    private final ActivityRepository activityRepository;
    private final ScoopPostRepository scoopPostRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;


    @Override
    public boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity) {
        return scoopPostRepository.existsByUserAndPost(userEntity, postEntity);
    }

    @Override
    public Optional<SpoonBalanceEntity> findByUser(UserEntity user) {
        return spoonBalanceRepository.findByUser(user);
    }
}
