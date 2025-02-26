package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.SignInUseCase;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.Provider;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements SignInUseCase {

    private final UserPort userPort;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserTokenDTO signIn(String providerToken, UserLoginDTO userLoginDTO) {
        PlatformUserDTO platformUserDTO = getPlatformInfo(providerToken, userLoginDTO);
        User user = userPort.loadOrCreate(userLoginDTO.provider(), platformUserDTO);
        JwtTokenDTO token = jwtTokenProvider.issueTokens(user.getUserId());
        saveToken(user.getUserId(), token);
        return UserTokenDTO.of(user, token);
    }

    private PlatformUserDTO getPlatformInfo(String providerToken, UserLoginDTO userLoginDto) {
        if (userLoginDto.provider().toString().equals("GOOGLE")){
            //TODO
        } else if (userLoginDto.provider().toString().equals("APPLE")){
            //TODO
        } else {
            throw new AuthException(AuthErrorMessage.PROVIDER_NOT_FOUND);
        }
        return null;
    }

    private User loadOrCreate(Provider provider, PlatformUserDTO platformUserDTO) {
        //TODO
        return null;
    }

    private void saveToken(Long userId, JwtTokenDTO token) {
        //TODO
    }
}
