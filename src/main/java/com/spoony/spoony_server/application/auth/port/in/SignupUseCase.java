package com.spoony.spoony_server.application.auth.port.in;

import com.spoony.spoony_server.adapter.auth.dto.request.UserSignupDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;

public interface SignupUseCase {
    UserTokenDTO signup(String platformToken, UserSignupDTO userSignupDTO);
}
