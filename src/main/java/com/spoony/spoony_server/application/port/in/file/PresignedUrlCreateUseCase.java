package com.spoony.spoony_server.application.port.in.file;

import com.spoony.spoony_server.adapter.dto.file.response.PresignedUrlResponseDTO;
import com.spoony.spoony_server.application.port.command.file.PresignedUrlCreateCommand;

public interface PresignedUrlCreateUseCase {
	PresignedUrlResponseDTO createPresignedUrl(PresignedUrlCreateCommand command);
}
