package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.application.port.command.zzim.ZzimAddCommand;

public interface ZzimAddUseCase {
    void addZzimPost(ZzimAddCommand command);
}
