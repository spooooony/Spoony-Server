package com.spoony.spoony_server.application.port.in.spoon;

import com.spoony.spoony_server.application.port.dto.spoon.SpoonResponseDTO;

public interface SpoonGetUseCase {
    SpoonResponseDTO getAmountById(Long userId);
}
