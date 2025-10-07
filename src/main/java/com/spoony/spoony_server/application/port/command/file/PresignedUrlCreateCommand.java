package com.spoony.spoony_server.application.port.command.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlCreateCommand {
	private final Long userId;
	private final String fileName;
	private final String contentType;
}
