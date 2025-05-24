package com.spoony.spoony_server.application.service.spoon;

import com.spoony.spoony_server.adapter.dto.spoon.SpoonDrawResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonDrawListResponseDTO;
import com.spoony.spoony_server.application.port.command.spoon.SpoonDrawCommand;
import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;
import com.spoony.spoony_server.application.port.in.spoon.SpoonDrawUseCase;
import com.spoony.spoony_server.application.port.in.spoon.SpoonGetUseCase;
import com.spoony.spoony_server.application.port.out.spoon.SpoonDrawPort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonBalancePort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonTypePort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;

import com.spoony.spoony_server.domain.spoon.SpoonDraw;
import com.spoony.spoony_server.domain.spoon.SpoonType;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class SpoonService implements SpoonGetUseCase, SpoonDrawUseCase {

    private final SpoonBalancePort spoonBalancePort;
    private final SpoonDrawPort spoonDrawPort;
    private final SpoonTypePort spoonTypePort;
    private final UserPort userPort;
    private final SpoonPort spoonPort;

    @Transactional
    public SpoonResponseDTO getAmountById(SpoonGetCommand command){
        SpoonBalance spoonBalance = spoonBalancePort.findBalanceByUserId(command.getUserId());
        return new SpoonResponseDTO(spoonBalance.getAmount());
    }

    @Transactional
    public SpoonDrawResponseDTO createDrawById(SpoonDrawCommand command){
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        // 중복 뽑기 확인
        boolean alreadyDrawn = spoonDrawPort.existsByUserIdAndDrawDate(command.getUserId(), today);
        if (alreadyDrawn) {
            SpoonDraw existingDraw = spoonDrawPort.findByUserIdAndDrawDate(command.getUserId(), today);
            return new SpoonDrawResponseDTO(
                    existingDraw.getDrawId(),
                    existingDraw.getSpoonType(),
                    existingDraw.getDrawDate(),
                    existingDraw.getWeekStartDate(),
                    existingDraw.getCreatedAt());
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
        Long drawId = spoonDrawPort.save(command.getUserId(), selectedType, today, weekStart);
        SpoonDraw spoonDraw = spoonDrawPort.findById(drawId);

        // 사용자 스푼 정보 변경
        User user = userPort.findUserById(command.getUserId());
        int amount = selectedType.getSpoonAmount();
        spoonPort.updateSpoonBalance(user, amount);
        spoonPort.updateSpoonHistory(user, amount);

        return new SpoonDrawResponseDTO(
                spoonDraw.getDrawId(),
                spoonDraw.getSpoonType(),
                spoonDraw.getDrawDate(),
                spoonDraw.getWeekStartDate(),
                spoonDraw.getCreatedAt());
    }

    @Transactional
    public SpoonDrawListResponseDTO getDrawById(SpoonDrawCommand command) {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        List<SpoonDraw> spoonDrawList = spoonDrawPort.findAllByUserIdAndWeekStartDate(command.getUserId(), weekStart);

        AtomicLong weeklyBalance = new AtomicLong(0L);

        List<SpoonDrawResponseDTO> spoonDrawResponseDTOList = spoonDrawList.stream()
                .map(spoonDraw -> {
                    weeklyBalance.addAndGet(spoonDraw.getSpoonType().getSpoonAmount());
                    return new SpoonDrawResponseDTO(
                            spoonDraw.getDrawId(),
                            spoonDraw.getSpoonType(),
                            spoonDraw.getDrawDate(),
                            spoonDraw.getWeekStartDate(),
                            spoonDraw.getCreatedAt()
                    );
                })
                .toList();

        SpoonBalance spoonBalance = spoonBalancePort.findBalanceByUserId(command.getUserId());
        Long weeklyBalanceValue = weeklyBalance.get();

        return new SpoonDrawListResponseDTO(spoonDrawResponseDTOList, spoonBalance.getAmount(), weeklyBalanceValue);
    }
}
