package com.spoony.spoony_server.application.service.spoon;

import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.application.port.dto.spoon.SpoonResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.spoon.jpa.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.jpa.SpoonBalanceRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpoonService implements SpoonGetUseCase {

    private final SpoonBalanceRepository spoonBalanceRepository;
    private final UserRepository userRepository;

    @Transactional
    public SpoonResponseDTO getAmountById(Long userId){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser(userEntity)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));

        return new SpoonResponseDTO(spoonBalanceEntity.getAmount());
    }
}
