package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.domain.user.Region;

public interface RegionPort {
    Region findByAddress(String address);
}
