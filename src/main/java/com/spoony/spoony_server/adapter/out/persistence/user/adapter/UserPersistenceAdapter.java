package com.spoony.spoony_server.adapter.out.persistence.user.adapter;

import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserRepository;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;

}
