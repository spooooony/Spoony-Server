package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.application.port.command.zzim.ZzimDeleteCommand;

public interface ZzimDeleteUseCase {
    void deleteZzim(ZzimDeleteCommand command);


}
