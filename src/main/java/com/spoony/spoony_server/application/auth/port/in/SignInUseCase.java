package com.spoony.spoony_server.application.auth.port.in;

import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;

public interface SignInUseCase {
    UserTokenDTO signIn( String providerToken, UserLoginDTO userLoginDTO);
}
