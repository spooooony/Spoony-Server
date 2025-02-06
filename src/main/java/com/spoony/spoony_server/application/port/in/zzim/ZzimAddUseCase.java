package com.spoony.spoony_server.application.port.in.zzim;

import com.spoony.spoony_server.application.port.dto.zzim.ZzimPostAddRequestDTO;

public interface ZzimAddUseCase {
    void addZzimPost(ZzimPostAddRequestDTO zzimPostAddRequest);
}
