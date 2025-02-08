package com.spoony.spoony_server.application.service.spoon;

import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpoonService implements SpoonGetUseCase {

    private final SpoonBalanceRepository spoonBalanceRepository;
    private final UserRepository userRepository;

    @Transactional
    public SpoonResponseDTO getAmountById(SpoonGetCommand command){
        UserEntity userEntity = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser(userEntity)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));

        return new SpoonResponseDTO(spoonBalanceEntity.getAmount());
    }
}
