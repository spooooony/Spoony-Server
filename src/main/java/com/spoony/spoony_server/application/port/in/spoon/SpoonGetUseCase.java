package com.spoony.spoony_server.application.port.in.spoon;

import com.spoony.spoony_server.adapter.dto.spoon.SpoonResponseDTO;
import com.spoony.spoony_server.application.port.command.spoon.SpoonGetCommand;

public interface SpoonGetUseCase {
    SpoonResponseDTO getAmountById(SpoonGetCommand command);
}
