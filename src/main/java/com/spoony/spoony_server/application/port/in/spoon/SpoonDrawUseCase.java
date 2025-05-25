package com.spoony.spoony_server.application.port.in.spoon;

import com.spoony.spoony_server.adapter.dto.spoon.response.SpoonDrawListResponseDTO;
import com.spoony.spoony_server.adapter.dto.spoon.response.SpoonDrawResponseDTO;
import com.spoony.spoony_server.application.port.command.spoon.SpoonDrawCommand;

public interface SpoonDrawUseCase {
    SpoonDrawResponseDTO createDrawById(SpoonDrawCommand command);
    SpoonDrawListResponseDTO getDrawById(SpoonDrawCommand command);
}
