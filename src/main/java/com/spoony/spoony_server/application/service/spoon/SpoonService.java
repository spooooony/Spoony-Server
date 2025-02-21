package com.spoony.spoony_server.application.service.spoon;

import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.application.port.out.spoon.SpoonBalancePort;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpoonService implements SpoonGetUseCase {

    private final SpoonBalancePort spoonBalancePort;

    @Transactional
    public SpoonResponseDTO getAmountById(SpoonGetCommand command){
        SpoonBalance spoonBalance = spoonBalancePort.findBalanceByUserId(command.getUserId());
        return new SpoonResponseDTO(spoonBalance.getAmount());
    }
}
