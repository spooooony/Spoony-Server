package com.spoony.spoony_server.application.service.spoon;

import com.spoony.spoony_server.adapter.dto.spoon.SpoonDrawResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonDrawListResponseDTO;
import com.spoony.spoony_server.application.port.command.spoon.SpoonDrawCommand;
import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.in.spoon.SpoonDrawUseCase;
import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.application.port.out.spoon.SpoonDrawPort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonBalancePort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonTypePort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;

import com.spoony.spoony_server.domain.spoon.SpoonDraw;
import com.spoony.spoony_server.domain.spoon.SpoonType;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpoonService implements SpoonGetUseCase, SpoonDrawUseCase {

    private final SpoonBalancePort spoonBalancePort;
    private final SpoonDrawPort spoonDrawPort;
    private final SpoonTypePort spoonTypePort;

    @Transactional
    public SpoonResponseDTO getAmountById(SpoonGetCommand command){
        SpoonBalance spoonBalance = spoonBalancePort.findBalanceByUserId(command.getUserId());
        return new SpoonResponseDTO(spoonBalance.getAmount());
    }

    @Transactional
    public SpoonDrawResponseDTO createDrawById(SpoonDrawCommand command){
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        Long userId = command.getUserId();

        // 중복 뽑기 확인
        boolean alreadyDrawn = spoonDrawPort.existsByUserIdAndDrawDate(userId, today);
        if (alreadyDrawn) {
            throw new BusinessException(SpoonErrorMessage.ALREADY_DRAWN);
        }

        // 모든 스푼 타입 조회
        List<SpoonType> spoonTypeList = spoonTypePort.findAll();
        if (spoonTypeList.isEmpty()) {
            throw new BusinessException(SpoonErrorMessage.SPOON_TYPE_NOT_FOUND);
        }

        // 스푼 타입 결정
        double random = Math.random() * 100;
        double accumulated = 0;
        SpoonType selectedType = null;

        for (SpoonType type : spoonTypeList) {
            accumulated += type.getProbability().doubleValue();
            if (random < accumulated) {
                selectedType = type;
                break;
            }
        }

        if (selectedType == null) {
            selectedType = spoonTypeList.getLast();
        }

        // 스푼 뽑기 결과 저장
        Long drawId = spoonDrawPort.save(userId, selectedType, today, weekStart);
        SpoonDraw spoonDraw = spoonDrawPort.findById(drawId);

        return new SpoonDrawResponseDTO(spoonDraw);
    }

    @Transactional
    public SpoonDrawListResponseDTO getDrawById(SpoonDrawCommand command) {
        Long userId = command.getUserId();
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        List<SpoonDraw> spoonDrawList = spoonDrawPort.findAllByUserIdAndWeekStartDate(userId, weekStart);

        return new SpoonDrawListResponseDTO(spoonDrawList);
    }
}
