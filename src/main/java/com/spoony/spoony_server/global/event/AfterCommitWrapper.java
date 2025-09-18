package com.spoony.spoony_server.global.event;

import com.spoony.spoony_server.application.event.PostCreatedEvent;

public record AfterCommitWrapper(PostCreatedEvent event) {
}
