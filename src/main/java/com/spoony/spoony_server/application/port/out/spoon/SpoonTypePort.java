package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonType;

import java.util.List;

public interface SpoonTypePort {
    List<SpoonType> findAll();
}
